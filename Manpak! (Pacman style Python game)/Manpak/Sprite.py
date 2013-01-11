import pygame
from pygame import *

#base sprite class we will use to easily initialize variables all sprites share
class Sprite(pygame.sprite.Sprite):
    def __init__(self, icon, coord):
        pygame.sprite.Sprite.__init__(self)
        self.image = icon
        self.rect = icon.get_rect()
        self.rect.center = coord

    
