import os

def create_folder(folder_path):
    try:
        os.makedirs(folder_path)  # 创建文件夹
        print('文件夹已创建:', folder_path)
    except OSError as e:
        print('创建文件夹失败:', e)

# 指定文件夹路径
folder_path = 'd:/desktop/fod'

# 创建文件夹
create_folder(folder_path)
