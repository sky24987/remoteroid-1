#pragma once

#define PORT				50000
#define UDPPORT				50001
#define MAXSIZE				4096
#define HEADERSIZE			6
#define OPCODESIZE			2
#define TOTALSIZE			4
#define FILENAMESIZE		100
#define FILESIZESIZE		100
#define JPGSIZELEGNTH		10


#define OP_SENDFILEINFO		1
#define OP_SENDFILEDATA		2
#define OP_SENDJPGINFO		3
#define OP_SENDJPGDATA		4
#define OP_REQFILEDATA		5
#define OP_READYSEND		6
#define OP_REQFILEINFO		7



#define WM_RECVJPGINFO		WM_USER+100
#define WM_RECVJPGDATA		WM_USER+101
#define WM_MYENDRECV		WM_USER+102
#define WM_MYENDACCEPT		WM_USER+103
#define WM_READYRECVFILE	WM_USER+104