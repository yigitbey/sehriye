//
//  XMPPTestViewController.m
//  XMPPTest
//
//  Created by Samet on 1/31/11.
//  Copyright 2011 JoopleBerry Inc. All rights reserved.
//

#import "XMPPTestViewController.h"
#import "XMPPTestAppDelegate.h"
#import <CoreLocation/CoreLocation.h>

@implementation XMPPTestViewController
@synthesize recieverTextField, chatTextView, messageField, switchConnectionButton, chatTableView, buttonsScroll, chatLog, currentStranger;
@synthesize currentLat, currentLong;

// The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
/*
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization.
    }
    return self;
}
*/


// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {

	buttonsScroll.contentSize = CGSizeMake(buttonsScroll.frame.size.width + 700, buttonsScroll.frame.size.height);
	[buttonsScroll setScrollEnabled:YES];
	
	[chatTextView setFrame:CGRectMake(160, 0, 320, 416)];

	
	chatLog = [[NSMutableArray alloc] init];
	
	xmppStream = [[self xmppStream] retain];
	[xmppStream addDelegate:self];
	[self getCurrentCoordinate];
    [super viewDidLoad];
}


/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations.
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
    // Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
    
    // Release any cached data, images, etc. that aren't in use.
}

- (void)viewDidUnload {
    [super viewDidUnload];
    // Release any retained subviews of the main view.
    // e.g. self.myOutlet = nil;
}



-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event{
	[recieverTextField resignFirstResponder];
	[messageField resignFirstResponder];
}

- (void)dealloc {
    [super dealloc];
}

- (void)scrollViewWillBeginDragging:(UIScrollView *)scrollView {
	
	if (scrollView != chatTableView)
		return;
	
	if(isTyping){
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
	self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y + 215, self.view.frame.size.width, self.view.frame.size.height);
	[buttonsScroll setHidden:NO];
	[messageField resignFirstResponder];
	[UIView commitAnimations];
		isTyping = NO;
	
	}
	
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField{
	
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
	self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y + 215, self.view.frame.size.width, self.view.frame.size.height);
	[buttonsScroll setHidden:NO];
	[textField resignFirstResponder];
	[UIView commitAnimations];
	
	[self performSelector:@selector(sendMessage:)];
	isTyping = NO;
	
	return YES;
	
}
- (void)textFieldDidBeginEditing:(UITextField *)textField{
	isTyping = YES;
	[UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
	self.view.frame = CGRectMake(self.view.frame.origin.x, self.view.frame.origin.y - 215, self.view.frame.size.width, self.view.frame.size.height);
	[buttonsScroll setHidden:YES];
	
	
	[UIView commitAnimations];

}

- (void)sendMessage:(id)sender{
	
	NSXMLElement *testMsg = [NSXMLElement elementWithName:@"message"];
	[testMsg addAttributeWithName:@"to" stringValue:@"samet.gltkn@gmail.com"];
	//[testMsg addAttributeWithName:@"from" stringValue:@"sobiyet.iphone@gmail.com"];
	[testMsg addAttributeWithName:@"type" stringValue:@"chat"];
	NSXMLElement *testMsgBody = [NSXMLElement elementWithName:@"body" stringValue:messageField.text];
	[testMsg addChild:testMsgBody];
	[[self xmppStream] sendElement:testMsg];
	
	NSMutableString *currString = [[NSMutableString alloc] initWithFormat:@"You:"];
		currString = (NSMutableString*)[currString stringByAppendingString:messageField.text];
		NSLog(@"currString %@", currString);
		[chatLog  addObject:currString];
		
	
	[chatTableView reloadData];
		//[chatTableView setContentOffset:CGPointMake(0, -[chatTableView contentSize].) animated:YES];
		//[chatTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:([chatLog count] - 0) inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];   
		
		NSInteger delta = [self.chatTableView contentSize].height -  self.chatTableView.bounds.size.height;
		CGPoint newContentOffset = CGPointMake(0, delta);
		
		if(delta > 0){
			[self.chatTableView setContentOffset:newContentOffset animated:YES]; 
		}
	
	[messageField setText:@""];
		
}

//- (void)connect:(id)sender{
//	
//	NSXMLElement *testMsg = [NSXMLElement elementWithName:@"message"];
//	[testMsg addAttributeWithName:@"to" stringValue:@"buluruzbirsey@appspot.com"];
//	//[testMsg addAttributeWithName:@"from" stringValue:@"sobiyet.iphone@gmail.com"];
//	[testMsg addAttributeWithName:@"type" stringValue:@"chat"];
//	NSXMLElement *testMsgBody = [NSXMLElement elementWithName:@"body" stringValue:@"PENCON:32:12"];
//	[testMsg addChild:testMsgBody];
//	[[self xmppStream] sendElement:testMsg];
//	
//	NSMutableString *currString = [[NSMutableString alloc] initWithString:chatTextView.text];
//	
//	currString = (NSMutableString*)[currString stringByAppendingFormat:@"\n - "];
//	currString = (NSMutableString*)[currString stringByAppendingString:messageField.text];
//	NSLog(@"currString %@", currString);
//	chatTextView.text = currString;
//	messageField.text = @"";
//	
//}


- (void) connect:(id)sender{
	
}

- (void) disconnect:(id)sender{
	
}

- (void)switchConnection:(id)sender{
	
	
	
	
	if (![sender isKindOfClass:[UIButton class]])
        return;
	
    NSString *title = [(UIButton *)sender currentTitle];
	UIButton *clickedButton = (UIButton *)sender;
	
	
	if([title isEqualToString:@"Start"]){
		
		[self performSelector:@selector(connect:)];
		clickedButton.titleLabel.text = @"Disconnect";
		[clickedButton setTitle:@"End" forState:UIControlStateNormal];
		NSLog(@"call connect");
		
	}
	
	else if([title isEqualToString:@"End"]){
		
		[self performSelector:@selector(disconnect:)];
		[clickedButton setTitle:@"Start" forState:UIControlStateNormal];
		NSLog(@"call disconnect");
	}
	
	
}

//
- (XMPPTestAppDelegate *)appDelegate
{
	return (XMPPTestAppDelegate *)[[UIApplication sharedApplication] delegate];
}

- (XMPPStream *)xmppStream
{
	return [[self appDelegate] xmppStream];
}


- (void)xmppStream:(XMPPStream *)sender didReceiveMessage:(XMPPMessage *)message{
	NSLog(@"---------- xmppStream:didReceiveMessage: ----------");
	NSLog(@"RECIEVED MESSAGEtoViewController:%@", [[message elementForName:@"body"] stringValue]);
	
	NSMutableString *currString;
	if([message isChatMessageWithBody]){
		currString = [[NSMutableString alloc] initWithFormat:@""];
		currString = (NSMutableString*)[currString stringByAppendingFormat:@"Stranger: "];
		currString = (NSMutableString*)[currString stringByAppendingString:[[message elementForName:@"body"] stringValue]];
		NSLog(@"currString %@", currString);
		[chatLog  addObject:currString];
		
		[chatTableView reloadData];
		//[chatTableView setContentOffset:CGPointMake(0, -[chatTableView contentSize].) animated:YES];
		//[chatTableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:([chatLog count] - 0) inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];   
		
		NSInteger delta = [self.chatTableView contentSize].height -  self.chatTableView.bounds.size.height;
		CGPoint newContentOffset = CGPointMake(0, delta);
		
		if(delta > 0){
			[self.chatTableView setContentOffset:newContentOffset animated:YES]; 
		}

		
		NSLog(@"CHATLOG:%@", chatLog);
	}
	
	
}

- (void)xmppStreamDidAuthenticate:(XMPPStream *)sender
{
	NSLog(@"---------- xmppStreamDidAuthenticate: ----------");
	
	[[self appDelegate] goOnline];
}


#pragma mark -
#pragma mark TableView delegate


- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
	
	return [chatLog count];
}

-(UITableViewCell*)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
	
	static NSString *CellIdentifier = @"Cell";
	
	UITableViewCell *cell = (UITableViewCell*)[tableView dequeueReusableCellWithIdentifier:CellIdentifier];
	if(cell == nil) 
	{
		cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
									   reuseIdentifier:CellIdentifier] autorelease];
	}
	
	if(chatLog){
		
		cell.textLabel.text = [chatLog objectAtIndex:[indexPath row]];
		[cell.textLabel setFont:[UIFont systemFontOfSize:17]];
	}
	
	
	return cell;
}


- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
	return 200;
}



#pragma mark -
#pragma mark coordinate
- (void)getCurrentCoordinate{
	
	// locationManager update as location
	CLLocationManager *locationManager = [[CLLocationManager alloc] init];
	locationManager.delegate = self; 
	locationManager.desiredAccuracy = kCLLocationAccuracyBest; 
	locationManager.distanceFilter = kCLDistanceFilterNone; 
	[locationManager startUpdatingLocation];
	
	CLLocation *location = [locationManager location];
	
	// Configure the new event with information from the location
	CLLocationCoordinate2D coordinate = [location coordinate];
	
	currentLat = [NSString stringWithFormat:@"%f", coordinate.latitude]; 
	currentLong = [NSString stringWithFormat:@"%f", coordinate.longitude];
	
	NSLog(@"Long:%@, Lat:%@", currentLong, currentLat);
}

@end
