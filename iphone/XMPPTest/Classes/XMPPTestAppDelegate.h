//
//  XMPPTestAppDelegate.h
//  XMPPTest
//
//  Created by Samet on 1/31/11.
//  Copyright 2011 JoopleBerry Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@class XMPPTestViewController;
@class XMPPStream;

@interface XMPPTestAppDelegate : NSObject <UIApplicationDelegate> {
	
	XMPPStream *xmppStream;
	NSString *password;
	
	BOOL allowSelfSignedCertificates;
	BOOL allowSSLHostNameMismatch;
	
	BOOL isOpen;
	
    UIWindow *window;
    XMPPTestViewController *viewController;
}

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) IBOutlet XMPPTestViewController *viewController;

@property (nonatomic, readonly) XMPPStream *xmppStream;

@end

