import matplotlib.pyplot as plt
import warnings

# 忽略警告
warnings.filterwarnings("ignore", category=UserWarning)

# 创建一个新图
plt.figure(figsize=(8, 6))

# 添加多层次目标的层次结构示意
# 以箭头表示不同层次目标之间的关系
plt.annotate("整体目标", (0.5, 0.9))
plt.annotate("子目标 1", (0.3, 0.7))
plt.annotate("子目标 2", (0.7, 0.7))
plt.annotate("子目标 3", (0.2, 0.5))
plt.annotate("子目标 4", (0.4, 0.5))
plt.annotate("子目标 5", (0.6, 0.5))
plt.annotate("子目标 6", (0.8, 0.5))

# 显示图表
plt.axis('off')
plt.show()
