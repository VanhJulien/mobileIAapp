from flask import Flask, render_template, request, jsonify
import os
from glob import glob
from argparse import ArgumentParser
from sklearn.preprocessing import LabelBinarizer, OneHotEncoder, LabelEncoder
from sklearn import preprocessing
import numpy as np
from tensorflow import keras
from keras.models import load_model
from keras.metrics import top_k_categorical_accuracy
import keras.applications as apps
from keras.applications.vgg16 import preprocess_input
import librosa
# from pydub import AudioSegment
import soundfile as sf

app = Flask(__name__)


@app.route("/upload", methods=["POST"])
def upload():
    if request.method == 'POST':
        print("Post request")
        file = request.files['audio_file']
        print(file)
        file.save(os.path.join('uploads', file.filename))
#         path = "./uploads/" + file.filename
        genre = recognition(r"D:\EPSI\B3\AtelierMobileIA\mobileIAapp\Api\uploads\recording.wav")

#         genre = recognition('D:\EPSI\B3\AtelierMobileIA\mobileIAapp\Api\demon.wav')

    else:
        print("Not Post request")
        genre = "Not Post request"

    return jsonify(style=genre)


def recognition(filename):
    model = load_model(os.path.abspath('myModel.h5'))
    print("-----------------------------------------------------------------------------------------------------")
    print(filename)
    audio_files = glob(filename)
    print(audio_files)
#     audio_file, sample_rate = librosa.load(audio_files[0], sr=None, mono=True, offset=0.0, duration=3.0)

#     sound = AudioSegment.from_file(r'D:\EPSI\B3\AtelierMobileIA\mobileIAapp\Api\uploads\recording.wav', format="wav")
#     audio_file = np.array(sound.get_array_of_samples())
#     sample_rate = sound.frame_rate

    audio_file, sample_rate = sf.read(filename)

    wnd_size = 512
    wnd_stride = 264

    chroma_stft = librosa.feature.chroma_stft(
        y=audio_file, sr=sample_rate, n_fft=wnd_size, win_length=wnd_size, hop_length=wnd_size).flatten()
    rms = librosa.feature.rms(
        y=audio_file, frame_length=wnd_size, hop_length=wnd_size).flatten()  # strange res
    spec_cent = librosa.feature.spectral_centroid(
        y=audio_file, sr=sample_rate, n_fft=wnd_size, win_length=wnd_size, hop_length=wnd_stride).flatten()  # strange res
    spec_bw = librosa.feature.spectral_bandwidth(
        y=audio_file, sr=sample_rate, n_fft=wnd_size, win_length=wnd_size, hop_length=wnd_stride).flatten()
    rolloff = librosa.feature.spectral_rolloff(
        y=audio_file + 0.01, sr=sample_rate, n_fft=wnd_size, win_length=wnd_size, hop_length=wnd_stride).flatten()  # strange var
    zcr = librosa.feature.zero_crossing_rate(
        audio_file, frame_length=wnd_size, hop_length=wnd_stride).flatten()  # strange res
    harmony = librosa.feature.tempogram(
        audio_file, win_length=wnd_size, hop_length=wnd_stride).flatten()  # strange res
    tempo = librosa.beat.tempo(audio_file)[0]
    perceptr = librosa.effects.percussive(audio_file)
    mfcc = librosa.feature.mfcc(y=audio_file, sr=sample_rate,
                                win_length=wnd_stride, hop_length=wnd_stride)  # strange res

    sample = [
        len(audio_file),
        chroma_stft.mean(),
        chroma_stft.var(),
        rms.mean(),
        rms.var(),
        spec_cent.mean(),
        spec_cent.var(),
        spec_bw.mean(),
        spec_bw.var(),
        rolloff.mean(),
        rolloff.var(),
        zcr.mean(),
        zcr.var(),
        harmony.mean(),
        harmony.var(),
        perceptr.mean(),
        perceptr.var(),
        tempo,
    ]
    print(sample)

    for i in range(0, 20):
        sample.append(np.mean(mfcc[i], axis=0))
        sample.append(np.var(mfcc[i], axis=0))

    prediction = model.predict(np.array(sample).reshape(1, -1))
    musicStyles = ["Blues", "Classical", "Country", "Disco", "HipHop", "Jazz", "Metal", "Pop", "Reggae", "Rock"]
    index = 0
    for i in prediction[0]:
        if i == 1.0:
            genre = musicStyles[index]
            print(genre)
        else:
            index +=1

    return genre


if __name__ == "__main__":
    app.run(host="192.168.1.17")
