import os,sys,levels,Sprite,random
import pygame
from pygame.locals import *
from handlers import *
from manpakSprite import *
from Beast import *

#Global Variables
BRICK = 24; #size of walls
levelDifficulty = "" #level choice (3 choices)

#basically my main function separated for clarity
class ManpakTopLevel:
	#defines window properties
    def __init__(self,width=640,height=480):
        pygame.init()
        self.width = width
        self.height = height
        self.screen = pygame.display.set_mode((self.width,self.height))
        pygame.display.set_caption("Manpak!")
	
	#main function starts up game, and keeps running within while loop
    def Main(self):
		#draws all background/non-moving images that only need be drawn once
        self.LoadSprites()
        self.background = pygame.Surface(self.screen.get_size())
        self.background = self.background.convert()
        self.background.fill((135,206,255))
        self.block_sprites.draw(self.screen)
        self.block_sprites.draw(self.background)
        self.clock = pygame.time.Clock()
        pygame.display.flip()
		#main loop where events (such as keystrokes) are handled
        while 1:
            self.clock.tick(30) #clock controls fps of game so it won't run too fast
            self.manpak_sprites.clear(self.screen,self.background)
            self.beast_sprites.clear(self.screen,self.background)
            for event in pygame.event.get():
				#events are checked for proper handling:
				#  music is updated during power up phases
				#  game is quit if user wins/
				#  user keystrokes are passed to the proper sprites for updating
                if event.type == pygame.QUIT:
                    pygame.quit()
                    return
                elif event.type == KEYDOWN:
                    if((event.key==K_RIGHT)or(event.key==K_DOWN)or(event.key==K_UP)or(event.key==K_LEFT)):
                        self.manpak.KEYDOWN(event.key)
                elif event.type == KEYUP:
                    if((event.key==K_RIGHT)or(event.key==K_DOWN)or(event.key==K_UP)or(event.key==K_LEFT)):
                        self.manpak.KEYUP(event.key)
                elif event.type == POWERED_DOWN:
                    pygame.mixer.music.load("music.mp3")
                    pygame.mixer.music.play(-1)
                    pygame.mixer.music.set_volume(1)
                    self.manpak.poweredUp = False
                    pygame.time.set_timer(POWERED_DOWN,0)
                    for beast in self.beast_sprites.sprites():
                        beast.SetState(False)
                elif event.type == POWERED_UP:
                    pygame.mixer.music.load("mortalkombat.mp3")
                    pygame.mixer.music.play(-1)
                    pygame.mixer.music.set_volume(0.2)
                    for beast in self.beast_sprites.sprites():
                        beast.SetState(True)
                elif event.type == MANPAK_KILLED:
                    print("YOU HAVE DIED. GAME OVER!")
                    pygame.quit()
                    return
                elif event.type == GAME_OVER:
                    print("CONGRATULATIONS! YOU HAVE WON!!")
                    pygame.quit()
                    return
            self.manpak_sprites.update(self.block_sprites,self.orb_sprites,self.powerUp_sprites,self.beast_sprites,self.orbTotal)
            self.beast_sprites.update(self.block_sprites)
            self.screen.blit(self.background,(0,0))
            textpos = 0
            font = pygame.font.Font(None,30)
            text = font.render("Orbs %s" % self.manpak.orbs, 1, (255, 0, 0))
            textpos = text.get_rect(centerx=self.background.get_width()/2,centery=self.background.get_height()-10)
            self.screen.blit(text,textpos) #draws the counter for the player to view
            newUpdates = [textpos]
            newUpdates += self.orb_sprites.draw(self.screen)
            newUpdates += self.powerUp_sprites.draw(self.screen)
            newUpdates += self.manpak_sprites.draw(self.screen)
            newUpdates += self.beast_sprites.draw(self.screen)
            pygame.display.update(newUpdates) #use of render updates instead of complete redrawing to increase performance
	
	#creates all sprites (orbs, beasts, blocks, power ups, and manpak)
	#every sprite is initialized from level layout (which is array backed)
    def LoadSprites(self):
		#loads music
        pygame.mixer.music.load("music.mp3")
        pygame.mixer.music.play(-1)
		#loads level and user layout
        level = levels.level1()
        if levelDifficulty == "easy":
            layout = level.getLayoutEasy()
        elif levelDifficulty == "normal":
            layout = level.getLayoutModerate()
        elif levelDifficulty == "hard":
            layout = level.getLayoutHard()
        else:
            layout = level.getLayoutHard()
		#initialize sprite variables and etc variables
        xcoord = (BRICK/2)
        ycoord = (BRICK/2)
        icons = level.getSprites()
        self.orb_sprites = pygame.sprite.RenderUpdates()
        self.block_sprites = pygame.sprite.RenderUpdates()
        self.powerUp_sprites = pygame.sprite.RenderUpdates()
        self.beast_sprites = pygame.sprite.RenderUpdates()
        self.orbTotal = 0
		#uses the arrays from the game board to load variables
        for i in xrange (len(layout)):
            for j in xrange (len(layout[i])):
                coord = [ i*BRICK + xcoord, j*BRICK + ycoord ]
                if layout[i][j] == level.MANPAK:
                    self.manpak = Manpak(icons[level.MANPAK],coord)
                elif layout[i][j] == level.ORB:
                    orb = Sprite.Sprite(icons[level.ORB],coord)
                    self.orb_sprites.add(orb)
                    self.orbTotal += 1
                elif layout[i][j] == level.BLOCK:
                    block = Sprite.Sprite(icons[level.BLOCK],coord)
                    self.block_sprites.add(block)
                elif layout[i][j] == level.BEAST:
                    beast = Beast(coord,icons[level.BEAST],icons[level.BEASTSCARED])
                    self.beast_sprites.add(beast)
                    orb = Sprite.Sprite(icons[level.ORB],coord)
                    self.orb_sprites.add(orb)
                    self.orbTotal += 1
                elif layout[i][j] == level.POWERUP:
                    powerUp = Sprite.Sprite(icons[level.POWERUP],coord)
                    self.powerUp_sprites.add(powerUp)
        self.manpak_sprites = pygame.sprite.RenderUpdates((self.manpak))

#orb class is simplistic and only loads an image upon creation
class Orb(pygame.sprite.Sprite):
    def __init__(self,rect=None):
        pygame.sprite.Sprite.__init__(self)
        self.image, self.rect = load_icon("orb.png",-1)
        if rect != None:
            self.rect = rect;


#starts main class and runs program
if __name__ == "__main__":
    levelDifficulty = raw_input("What difficulty would you like (easy,normal,hard)? ")
    TopLevel = ManpakTopLevel()
    TopLevel.Main()

