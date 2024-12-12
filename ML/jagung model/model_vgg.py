"""### inport"""

# Import pustaka utama
import tensorflow as tf
import numpy as np
import pandas as pd
# import cv2
import matplotlib.pyplot as plt
from tensorflow.keras.callbacks import ModelCheckpoint

"""### load datataset

"""

dataset_path = './ML/jagung model/dataset'
img_size = (224, 224)

# 90% training, 5% validation, 5% test
train_dataset = tf.keras.preprocessing.image_dataset_from_directory(
    dataset_path,
    validation_split=0.10,
    subset="training",
    seed=123,
    label_mode='categorical',
    image_size=img_size,
    batch_size=None
)

val_test_dataset = tf.keras.preprocessing.image_dataset_from_directory(
    dataset_path,
    validation_split=0.10,
    subset="validation",
    seed=123,
    label_mode='categorical',
    image_size=img_size,
    batch_size=None
)

#val test bagi 2
val_batches = int(0.5 * len(val_test_dataset)) 

#val data set
validation_dataset = val_test_dataset.take(val_batches)
#test data set
test_dataset = val_test_dataset.skip(val_batches)

"""visual shape"""

print("Train dataset len:", len(train_dataset))
print("Validation dataset len:", len(validation_dataset))
print("Test dataset len:", len(test_dataset))
for images, labels in train_dataset.take(1):
    print("Train dataset shape:", images.shape)
    print("Train labels shape:", labels.shape)

"""### preprocecing"""

# ubah ke rentang warna 0-1
def preprocess_image(image, label):
    image = tf.cast(image, tf.float32) / 255.0
    return image, label

batch_size = 32
train_data = train_dataset.map(preprocess_image, num_parallel_calls=tf.data.AUTOTUNE).batch(batch_size).shuffle(100).cache().prefetch(tf.data.AUTOTUNE)
validation_data = validation_dataset.map(preprocess_image, num_parallel_calls=tf.data.AUTOTUNE).batch(batch_size).cache().prefetch(tf.data.AUTOTUNE)
test_data = test_dataset.map(preprocess_image, num_parallel_calls=tf.data.AUTOTUNE).batch(batch_size).cache().prefetch(tf.data.AUTOTUNE)


# Ambil satu data dari train_data
image, label = next(iter(train_data.take(1)))

print("Image shape:", image.shape)
print("Pixel values (first 10 pixels after normalization):", image[0, :10, 0].numpy())  # Tampilkan 10 pixel pertama pada channel pertama

"""### get model from tensorflow """

base_model = tf.keras.applications.VGG16(
    include_top=False,
    weights='imagenet', 
    input_shape=(224, 224, 3)
)
for layer in base_model.layers:  # Bekukan kecuali layer
    layer.trainable = False


# base_model.summary()

"""### create model"""

x=tf.keras.layers.GlobalAveragePooling2D()(base_model.output)
x=tf.keras.layers.Dense(1024,activation='relu')(x)
# x=tf.keras.layers.Dropout(0.2)(x)
x=tf.keras.layers.Dense(4,activation='softmax')(x)
model=tf.keras.models.Model(inputs=base_model.input,outputs=x)

# Compile model
model.compile(
    optimizer=tf.keras.optimizers.Adam(learning_rate=0.001),
    loss='categorical_crossentropy',
    metrics=['accuracy']
)


# Cek model summary
model.summary()
# model.load_weights('/content/best_model.h5')


"""### training"""
# Membuat folder untuk menyimpan model jika belum ada
import os
model_dir = './ML/jagung model/saved model'
if not os.path.exists(model_dir):
    os.makedirs(model_dir)

# Callback untuk menyimpan model terbaik berdasarkan validasi akurasi
checkpoint_callback = ModelCheckpoint(
    filepath=os.path.join(model_dir, 'best_model-03.keras'), 
    save_best_only=True,  
    monitor='val_accuracy',  
    mode='max',  
    save_weights_only=False,  
    verbose=1  
)


#Melatih model
history = model.fit(
    train_data,  
    validation_data=validation_data,
    epochs=20,
    verbose=1 ,
    callbacks=[checkpoint_callback]
)

"""viualisasi"""

# Grafik untuk Loss
plt.figure(figsize=(12, 6))

plt.subplot(1, 2, 1)  # Subplot pertama untuk loss
plt.plot(history.history['loss'], label='Train Loss')
plt.plot(history.history['val_loss'], label='Validation Loss')
plt.title('Model Loss')
plt.xlabel('Epochs')
plt.ylabel('Loss')
plt.legend()

# Grafik untuk Accuracy
plt.subplot(1, 2, 2)  # Subplot kedua untuk accuracy
plt.plot(history.history['accuracy'], label='Train Accuracy')
plt.plot(history.history['val_accuracy'], label='Validation Accuracy')
plt.title('Model Accuracy')
plt.xlabel('Epochs')
plt.ylabel('Accuracy')
plt.legend()

# Tampilkan grafik
plt.tight_layout()
plt.show()

"""### test set"""

# Evaluasi model pada test data
test_loss, test_accuracy = model.evaluate(test_data)  
print(f'Test Loss: {test_loss}')
print(f'Test Accuracy: {test_accuracy}')


"""### visual 5 data"""

unbatched_data = list(test_data.unbatch())
# Assuming the first two elements of each unbatched item are image and label
data=20
image_batch = [item[0] for item in unbatched_data[-data:]]
label_batch = [item[1] for item in unbatched_data[-data:]]

# model= tf.keras.models.load_model('/content/best_model.keras')

# Prediksi untuk gambar batch
predictions = model.predict(np.array([image.numpy() for image in image_batch]))  # Prediksi dengan model

# Menampilkan gambar dan prediksinya
plt.figure(figsize=(20, 12))

for i in range(len(image_batch)):
    plt.subplot(4, 5, i+1)

    # Kembalikan gambar ke rentang asli (0-255)
    image_display = image_batch[i].numpy() * 255.0
    image_display = np.clip(image_display, 0, 255).astype("uint8")

    plt.imshow(image_display)
    predicted_class = np.argmax(predictions[i])  # Ambil kelas yang diprediksi
    true_class = np.argmax(label_batch[i])  # Ambil kelas yang benar
    plt.title(f'Pred: {predicted_class}, True: {true_class}')
    plt.axis('off')

plt.show()