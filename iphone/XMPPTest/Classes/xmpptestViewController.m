//
//  XMPPTestViewController.m
//  XMPPTest
//
//  Created by Samet on 1/31/11.
//  Copyright 2011 JoopleBerry Inc. All rights reserved.
//

#import "XMPPTestViewController.h"
#import "XMPPTestAppDelegate.h"

@implementation XMPPTestViewController
@synthesize recieverTextField, chatTextView, messageField, sendButton;

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
	chatTextView.text = @"deneme";
	xmppStream = [[self xmppStream] retain];
	[xmppStream addDelegate:self];
	
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

-(BOOL)textFieldShouldReturn:(UITextField *)textField{
	
	
	
	return YES;
	
}

- (void)sendMessage:(id)sender{
	
	NSXMLElement *testMsg = [NSXMLElement elementWithName:@"message"];
	[testMsg addAttributeWithName:@"to" stringValue:recieverTextField.text];
	[testMsg addAttributeWithName:@"from" stringValue:@"sobiyet.iphone@gmail.com"];
	[testMsg addAttributeWithName:@"type" stringValue:@"chat"];
	NSXMLElement *testMsgBody = [NSXMLElement elementWithName:@"body" stringValue:messageField.text];
	[testMsg addChild:testMsgBody];
	[[self xmppStream] sendElement:testMsg];
	
	NSMutableString *currString = [[NSMutableString alloc] initWithString:chatTextView.text];

	currString = (NSMutableString*)[currString stringByAppendingFormat:@"\n - "];
	currString = (NSMutableString*)[currString stringByAppendingString:messageField.text];
	NSLog(@"currString %@", currString);
	chatTextView.text = currString;
	messageField.text = @"";
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
	NSMutableString *currString = [[NSMutableString alloc] initWithString:chatTextView.text];
	if([message isChatMessageWithBody]){
		currString = (NSMutableString*)[currString stringByAppendingFormat:@"\n + "];
		currString = (NSMutableString*)[currString stringByAppendingString:[[message elementForName:@"body"] stringValue]];
		NSLog(@"currString %@", currString);
		chatTextView.text = currString;
	}
	
	
}

- (void)xmppStreamDidAuthenticate:(XMPPStream *)sender
{
	NSLog(@"---------- xmppStreamDidAuthenticate: ----------");
	
	[[self appDelegate] goOnline];
}

@end
