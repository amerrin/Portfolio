import pygame,Sprite,random

#beast class which will "hunt" manpak
class Beast(Sprite.Sprite):
	#initializes variables and images
    def __init__(self,coord,icon,secondIcon=None):
        Sprite.Sprite.__init__(self,icon,coord)
        self.compass = random.randint(1,4)
        self.stepCounter = 0
        self.step = 3
        self.stepCount = random.randint(125,175)
        self.image = icon
        self.firstImage = icon
        self.startRect = pygame.Rect(self.rect)
        if secondIcon != None:
            self.secondImage = secondIcon
        else:
            self.secondImage = icon
        self.scared = False
	
	#used to update the beasts to "scared" state when manpak gets a power up or powers down
    def SetState(self,state):
        if self.scared != state:
            self.scared = state
            if state == True:
                self.image = self.secondImage
            else:
                self.image = self.firstImage
	
	#used to "kill" the beast and move him off the board
    def Eaten(self):
        self.scared = False
        self.image = self.firstImage
        self.rect = self.startRect
	
	#this is the primary movement function for beasts
	#beasts move randomly when they hit walls, or when they reach their randomized step count
    def update(self,blocks):
        deltaX = 0;
        deltaY = 0;
        if self.compass == 1:
            deltaX = -self.step
        elif self.compass == 2:
            deltaY = -self.step
        elif self.compass == 3:
            deltaX = self.step
        elif self.compass == 4:
            deltaY = self.step
        self.rect.move_ip(deltaX,deltaY)
        self.stepCounter = self.stepCounter + 1
        if pygame.sprite.spritecollideany(self,blocks):
            self.compass = random.randint(1,4)
            self.rect.move_ip(-deltaX,-deltaY)
        elif self.stepCount == self.stepCounter:
            self.compass = random.randint(1,4)
            self.stepCounter = 0
            self.stepCount = random.randint(125,175)
