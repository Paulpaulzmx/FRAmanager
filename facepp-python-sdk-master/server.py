from flask import Flask
from flask import request
from flask import json
import os
import pymysql

from pprint import pformat

# import PythonSDK
from PythonSDK.facepp import API, File

# 图片暂存
img_transmit = './static/transmit.jpg'
img_register = './static/register.jpg'

basedir = os.path.abspath (os.path.dirname (__file__))  # 定义一个根目录 用于保存图片用



# api
api = API ()

app = Flask (__name__)


# 此方法专用来打印api返回的信息
def print_result(hit, result):
    print (hit)
    print ('\n'.join ("  " + i for i in pformat (result, width=75).split ('\n')))


def print_function_title(title):
    return "\n" + "-" * 60 + title + "-" * 60


# 接受上传图片并据此进行人脸比对，返回检测到人的user_id
@app.route ('/transmit', methods=['GET', 'POST'])
def transmit():
    # 获取图片文件 name = transmit
    img = request.files.get ('transmit')
    

    # 定义一个图片存放的位置 存放在static下面
    path = basedir + "\static\\"

    # 图片名称
    img_name = 'transmit.jpg'

    # 图片path和名称组成图片的保存路径
    file_path = path + img_name

    # 保存图片
    img.save (file_path)

    # 开始搜索相似脸人脸信息
    search_result = api.search (image_file=File (img_transmit), outer_id='check on work')
    print_result ('search', search_result)

    # 整个json转化为字符串
    search_result = json.dumps (eval (str (search_result)))
    # 整个json转化为字典
    search_result = json.loads (search_result)
    print(search_result)

    # 如果成功（返回值中有results字段），返回user_id
    if 'results' in search_result:
        # results转化为字符串
        results = json.dumps (eval (str (search_result['results'])))
        # results去除首位的方括号
        results = results[1:-1]
        # result转化为元组
        results = json.loads (results)

        user_id = results["user_id"]

        # 判断结果可信度
        if results['confidence'] > 80:

            # todo 签到记录写入数据库
            # SQL 插入语句
            # mysql 初始化
            db = pymysql.connect (host="localhost",
                                  user="root",
                                  password="zdhwjfln",
                                  db="check_on_work")
            # 加入数据库作为历史纪录
            try:
                with db.cursor() as cursor:
                    sql = "INSERT INTO `register` (`student_id`) VALUES ('%s')" % (user_id)
                    cursor.execute (sql)
                    db.commit()
            finally:
                db.close()
                pass

            # 返回user_id
            return '签到成功：' + user_id
        else:
            return '验证失败'

    # 失败返回错误信息
    elif 'error_message' in search_result:
        error = search_result['error_message']
        return error
    elif not search_result['faces']:
        return '未识别到人脸'
    else:
        return '未知错误'


# 注册新用户（添加人脸）
@app.route ('/register', methods=['GET', 'POST'])
def register():
    # 获取图片文件 name = register
    img = request.files.get ('register')
    path = basedir + "\static\\"
    img_name = 'register.jpg'
    file_path = path + img_name
    img.save (file_path)

    # 人脸检测：https://console.faceplusplus.com.cn/documents/4888373
    detect_result = api.detect (image_file=File (img_register), return_attributes="")
    # print_result (print_function_title ("人脸检测"), detect_result)

    # 获取face_token
    detect_result = json.dumps (eval (str (detect_result)))
    detect_result = json.loads (detect_result)

    # 如果成功，返回值里有faces字段
    if 'faces' in detect_result:
        faces = json.dumps (eval (str (detect_result['faces'])))
        faces = faces[1:-1]
        faces = json.loads (faces)

        face_token = faces['face_token']
        print ('新上传的face_token' + face_token)

        # 根据face_token设置user_id
        user_id = request.form['user_id']
        # Face set_user_id
        set_id_result = api.face.setuserid (face_token=str (face_token), user_id=str (user_id))
        print ('set_user_id的结果：', set_id_result)

        # 将新建的用户加入FaceSet
        add_face_result = api.faceset.addface (face_tokens=face_token, outer_id='check on work')
        add_face_result = json.dumps (eval (str (add_face_result)))
        add_face_result = json.loads (add_face_result)
        print ('新增数目：', add_face_result['face_added'])

        return '新学生 ' + user_id + ' 注册成功'

    # 请求错误（有错误码）
    elif 'error_message' in detect_result:

        error = detect_result['error_message']
        print (error)

        return '识别失败，请重试！'

    # 其他错误（网络等原因）
    else:
        return '未知错误'


# 查询历史纪录
@app.route('/history', methods=['GET'])
def history_query():
    # mysql 初始化
    db = pymysql.connect (host="localhost",
                          user="root",
                          password="zdhwjfln",
                          db="check_on_work")
    try:
        with db.cursor() as cursor:
            sql = "SELECT * FROM `register`"
            cursor.execute(sql)
            history_result =cursor.fetchall()
            db.commit()
    finally:
        db.close()

    histories = []
    for i in history_result:
        history = {'user_id': i[1], 'register_time': i[2]}
        histories.append(history)

    jsonStr = json.dumps(histories)

    return jsonStr


if __name__ == '__main__':
    app.run (port=80)
