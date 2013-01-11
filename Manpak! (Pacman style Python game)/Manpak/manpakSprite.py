import pygame,Sprite
from pygame import *

#events created so that they may be raised manually/by timer for the main loop to catch
POWERED_UP = pygame.USEREVENT + 1
POWERED_DOWN = POWERED_UP + 1
MANPAK_KILLED = POWERED_DOWN + 1
GAME_OVER = MANPAK_KILLED + 1

#player class
class Manpak(Sprite.Sprite):
    def __init__(self,icon,coord):
        Sprite.Sprite.__init__(self,icon,coord)
        self.orbs = 0
        self.y_step = 3
        self.x_step = 3
        self.deltaY = 0
        self.deltaX = 0
       
	#triggered when a user pushes (and keeps pressed) a button
    def KEYDOWN(self,key):
        if(key == K_UP):
            self.deltaY += -self.y_step
        elif(key == K_DOWN):
            self.deltaY += self.y_step
        elif(key == K_RIGHT):
            self.deltaX += self.x_step
        elif(key == K_LEFT):
            self.deltaX += -self.x_step
	
	#counteracts the down keystrokes so that the player will stop moving properly
    def KEYUP(self,key):
        if(key==K_UP):
            self.deltaY += self.y_step
        elif (key==K_DOWN):
            self.deltaY += -self.y_step
        elif (key==K_LEFT):
            self.deltaX += self.x_step
        elif (key==K_RIGHT):
            self.deltaX += -self.x_step
	
	#manpaks movement function
	#  will prevent him from moving "into" walls
	#  will kill manpak if he runs into a beast
	#  will keep track of manpaks orbs he collides with (AKA "collects")
	#  starts the appropriate timers/events when manpak hits a power up
    def update(self,blocks,orbs,powerUps,beasts,totalOrbs):
        if(self.deltaX==0 and self.deltaY==0):
            return
        self.rect.move_ip(self.deltaX,self.deltaY)
        if pygame.sprite.spritecollide(self,blocks,False):
            self.rect.move_ip(-self.deltaX,-self.deltaY)
        beastsHit = pygame.sprite.spritecollide(self,beasts,False)
        if(len(beastsHit) > 0):
            self.BeastsHit(beastsHit)
        else:
            orbsGathered = pygame.sprite.spritecollide(self,orbs,True)
            powerUpsHit = pygame.sprite.spritecollide(self,powerUps,True)
            if(len(orbsGathered) > 0):
                self.orbs = self.orbs + len(orbsGathered)
                if (self.orbs == totalOrbs):
                    pygame.event.post(pygame.event.Event(GAME_OVER,{}))
            elif(len(powerUpsHit) > 0):
                self.poweredUp = True
                pygame.event.post(pygame.event.Event(POWERED_UP,{}))
                pygame.time.set_timer(POWERED_DOWN,0)
                pygame.time.set_timer(POWERED_DOWN,5000)
	
	#triggered when a beast is hit and raises a "killed" event
    def BeastsHit(self,beastsHit):
        for beast in beastsHit:
            if (beast.scared == True):
                beast.Eaten()
            else:
                pygame.event.post(pygame.event.Event(MANPAK_KILLED,{}))
