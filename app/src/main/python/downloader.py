# import yt_dlp
# import os
#
# def descargar(url, mode, path):
#
#     ydl_opts = {
#         'outtmpl': os.path.join(path, '%(title)s.%(ext)s'),
#         'noplaylist': True
#     }
#
#     if mode == "video":
#         ydl_opts['format'] = 'bv*+ba/b'
#         ydl_opts['merge_output_format'] = 'mp4'
#
#     elif mode == "audio":
#         ydl_opts['format'] = 'bestaudio'
#         ydl_opts['postprocessors'] = [{
#             'key': 'FFmpegExtractAudio',
#             'preferredcodec': 'mp3'
#         }]
#
#     with yt_dlp.YoutubeDL(ydl_opts) as ydl:
#         ydl.download([url])
#
#     return "ok"

import yt_dlp
import os

def descargar(url, mode, path):
    ydl_opts = {
        'outtmpl': os.path.join(path, '%(title)s.%(ext)s'),
        'noplaylist': True
    }

    if mode == "video":
        # 'best' descarga la mejor calidad que YA trae video y audio unidos.
        # ¡Así no necesitamos a FFmpeg para pegarlos!
        ydl_opts['format'] = 'best'

    elif mode == "audio":
        # 'bestaudio' baja el audio original (usualmente .m4a o .webm)
        # Android reproduce estos formatos a la perfección sin tener que convertirlos a MP3.
        ydl_opts['format'] = 'bestaudio/best'

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])

    return "ok"
