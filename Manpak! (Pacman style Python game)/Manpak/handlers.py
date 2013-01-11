import os,sys
import pygame
from pygame import *

#bridges the images file with the proper pathname to load image
def load_icon(name, colorkey=None):
    path = os.path.join('images', name)
    try:
        image = pygame.image.load(path)
    except pygame.error, message:
        print 'Cannot load icon:', name
        raise SystemExit, message
    image = image.convert()
    if colorkey is not None:
        if colorkey is -1:
            colorkey = image.get_at((0,0))

        image.set_colorkey(colorkey, RLEACCEL)
    return image, image.get_rect()
