import yt_dlp
import os

def descargar(url, mode, path):
    ydl_opts = {
        'outtmpl': os.path.join(path, '%(title)s.%(ext)s'),
        'noplaylist': True,
        'updatetime': False,
        'nocheckcertificate': True,
        'quiet': True,
        'no_warnings': True
    }

    if mode == "video":
        ydl_opts['format'] = 'best'
    elif mode == "audio":
        ydl_opts['format'] = 'bestaudio[ext=m4a]/bestaudio'

    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=True)
        # Obtenemos el nombre del archivo final
        filename = ydl.prepare_filename(info)
        return os.path.basename(filename)
