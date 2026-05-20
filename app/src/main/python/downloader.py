import yt_dlp
import os

def descargar(url, mode, path):
    ydl_opts = {
        'outtmpl': os.path.join(path, '%(title)s.%(ext)s'),
        'noplaylist': True
    }

    if mode == "video":
        # 'best' descarga la mejor calidad que YA trae video y audio unidos.
        ydl_opts['format'] = 'best'

    elif mode == "audio":
        # 'bestaudio' baja el audio original a .m4a
        ydl_opts['format'] = 'bestaudio[ext=m4a]/bestaudio'

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        ydl.download([url])

    return "ok"
