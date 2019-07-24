import pymysql


def init():
    db = pymysql.connect("localhost", "root", "zdhwjfln", "check_on_work")
    cursor = db.cursor()


init()
