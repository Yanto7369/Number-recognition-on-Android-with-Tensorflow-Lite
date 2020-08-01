import numpy as np
import tensorflow as tf
from PIL import Image
import numpy as np
import pprint

# 元となる画像の読み込み
sample_path=""
# TFLiteモデルの読み込み
model_path=""

img = Image.open(sample_path)
img_resize=img.resize((28,28))
imgdata=np.asarray(img_resize,dtype=np.float32)
im_gray = (0.299 * imgdata[:, :, 0] + 0.587 * imgdata[:, :, 1] + 0.114 * imgdata[:, :, 2])/255 #グレースケール化と正規化
imgdata2=im_gray[None,:,:,None]#配列を1,28,28,1に整形
interpreter = tf.lite.Interpreter(model_path)
# メモリ確保。これはモデル読み込み直後に必須
interpreter.allocate_tensors()
# 学習モデルの入力層・出力層のプロパティをGet.
input_details = interpreter.get_input_details()
output_details = interpreter.get_output_details()
# テンソルデータ構成から、ランダムな ndArrayを作成
# np.arrayのcall時に、input_detailsのdtypeと整合性が取れるように型をセットしないと、set_tensor時にエラーが発生
#print(input_data)
# indexにテンソルデータのポインタをセット
interpreter.set_tensor(input_details[0]['index'], imgdata2)

# 推論実行
interpreter.invoke()
# 推論結果は、output_detailsのindexに保存されている
output_data = interpreter.get_tensor(output_details[0]['index'])
print("確率")
for i,ele in enumerate(np.round(output_data,3)[0]):
    print(str(i)+"   "+ str(ele))
print("予測結果"+str(np.argmax(output_data[0])))