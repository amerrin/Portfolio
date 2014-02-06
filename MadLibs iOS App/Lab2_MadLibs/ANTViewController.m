//
//  ANTViewController.m
//  Madlibs iOS App
//
//  Created by ITP Student on 9/11/13.
//  Copyright (c) 2013 ITP. All rights reserved.
//

#import "ANTViewController.h"

@interface ANTViewController ()
@property (weak, nonatomic) IBOutlet UITextField *nameTF;
@property (weak, nonatomic) IBOutlet UITextField *placeTF;
@property (weak, nonatomic) IBOutlet UITextField *ageTF;
@property (weak, nonatomic) IBOutlet UIView *moreView;
@property (weak, nonatomic) IBOutlet UILabel *sliderLabel;
@property (weak, nonatomic) IBOutlet UISegmentedControl *animalSG;
@property (weak, nonatomic) IBOutlet UILabel *petLabel;
@property (weak, nonatomic) IBOutlet UISwitch *happyEndingSwitch;

@end

@implementation ANTViewController
- (IBAction)moreSGTouched:(id)sender {
    UISegmentedControl *moreSG = (UISegmentedControl *) sender;
    if (moreSG.selectedSegmentIndex == 0)
    {
        [self.moreView setHidden:YES];
    } else {
        [self.moreView setHidden:NO];
    }
}
- (IBAction)slideTouched:(id)sender {
    UISlider *numSlider = (UISlider *) sender;
    int numAsInt = (int) (numSlider.value + 0.5f);
    NSString *newText = [[NSString alloc] initWithFormat:@"%d", numAsInt];
    self.sliderLabel.text = newText;
}
- (IBAction)stepperTouched:(id)sender {
    UIStepper *petStepper = (UIStepper *) sender;
    int stepperNum = (int) petStepper.value;
    NSString *numPets = [[NSString alloc] initWithFormat:@"%d", stepperNum];
    self.petLabel.text = numPets;
}
- (IBAction)createStoryTouched:(id)sender {
    UIActionSheet *actionSheet = [[UIActionSheet alloc]
                                  initWithTitle:@"Are you ready for your story?"
                                  delegate:self
                                  cancelButtonTitle:@"Not Yet"
                                  destructiveButtonTitle:@"Yes!"
                                  otherButtonTitles:nil];
    [actionSheet showInView:self.view];
}
- (IBAction)nameTFDoneEditing:(id)sender {
}
- (IBAction)placeTFDoneEditing:(id)sender {
}
- (IBAction)ageTFDoneEditing:(id)sender {
}
- (IBAction)backgroundButtonTouched:(id)sender {
    [self.nameTF resignFirstResponder];
    [self.placeTF resignFirstResponder];
    [self.ageTF resignFirstResponder];
}

- (void)actionSheet:(UIActionSheet *)actionSheet didDismissWithButtonIndex:(NSInteger)buttonIndex {
    if (buttonIndex != [actionSheet cancelButtonIndex]){
        [self createStory];
    }
}

- (void)createStory{
    NSString *selectedAnimal = [[NSString alloc] initWithFormat:@""];
    switch (self.animalSG.selectedSegmentIndex){
        case 0:
            selectedAnimal = @"koala";
            break;
        case 1:
            selectedAnimal = @"giraffe";
            break;
        case 2:
            selectedAnimal = @"panda";
            break;
        case 3:
            selectedAnimal = @"flamingo";
            break;
    }
    
    NSString *ending;
    
    if (self.happyEndingSwitch.on == TRUE){
        ending = [[NSString alloc] initWithFormat:@"%@ spoke with the zookeeper. The keeper was reluctant to lose the %@, but obliged to the adoption in the spirit of love.",self.nameTF.text, selectedAnimal];
    }
    else{
        ending = [[NSString alloc] initWithFormat:ending = @"Unfortunately, a wild %@ does not make a good pet. The zookeeper denied the adoption request, and %@ left the zoo sad and empty handed.", selectedAnimal, self.nameTF.text];
    }
    
    NSString *story =[[NSString alloc] initWithFormat:@"One day %@ really wanted a vacation. So, %@ decided to visit %@. During the trip, %@ heard of a local %@ display. Although the zoo was charging %@ dollars for entry, it would be worth every penny. Having %@ animals, and being the ripe age of %@, %@ thought it would be a good idea to adopt the wild %@ as a life companion. %@", self.nameTF.text, self.nameTF.text, self.placeTF.text, self.nameTF.text, selectedAnimal, self.sliderLabel.text, self.petLabel.text,self.ageTF.text,self.nameTF.text,selectedAnimal, ending];
    
    UIAlertView *alertView1 = [[UIAlertView alloc]
                                initWithTitle:@"MadLibs Story"
                                message:story
                                delegate:nil
                                cancelButtonTitle:@"Ok"
                                otherButtonTitles:nil];
    [alertView1 show];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
