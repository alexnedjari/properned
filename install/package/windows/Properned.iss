;This file will be executed next to the application bundle image
;I.e. current directory will contain folder Properned with application files
[Setup]
AppId={{application}}
AppName=Properned
AppVersion=3.1
AppVerName=Properned 3.1
AppPublisher=Alexandre NEDJARI
AppComments=Properned
AppCopyright=Copyright (C) 2015
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
;DefaultDirName={localappdata}\Properned
DefaultDirName={pf}\Properned
DisableStartupPrompt=true
DisableDirPage=false
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=false
DisableWelcomePage=false
DefaultGroupName=Alexandre NEDJARI
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=Properned-3.1
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=Properned\Properned.ico
UninstallDisplayIcon={app}\Properned.ico
UninstallDisplayName=Properned
WizardImageStretch=Yes
WizardImageFile=Properned-setup-icon.bmp   
;WizardSmallImageFile=Properned-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=


[Languages]
;Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}";

[Dirs]
Name: "{app}"; Permissions : users-modify

[Files]
Source: "Properned\Properned.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "Properned\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\Properned"; Filename: "{app}\Properned.exe"; IconFilename: "{app}\Properned.ico"; 
;Check: returnTrue()
Name: "{commondesktop}\Properned"; Filename: "{app}\Properned.exe";  IconFilename: "{app}\Properned.ico"; 
;Check: returnFalse()


[Run]
;Filename: "{app}\Properned.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\Properned.exe"; Description: "{cm:LaunchProgram,Properned}"; Flags: nowait postinstall skipifsilent; 
;Check: returnTrue()
;Filename: "{app}\Properned.exe"; Parameters: "-install -svcName ""Properned"" -svcDesc ""Properned"" -mainExe ""Properned.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\Properned.exe "; Parameters: "-uninstall -svcName Properned -stopOnUninstall"; 
;Check: returnFalse()

;[Code]
;function returnTrue(): Boolean;
;begin
;  Result := True;
;end;

;function returnFalse(): Boolean;
;begin
;  Result := False;
;end;

;function InitializeSetup(): Boolean;
;begin
;// Possible future improvements:
;//   if version less or same => just launch app
;//   if upgrade => check if same app is running and wait for it to exit
;//   Add pack200/unpack200 support? 
;  Result := True;
;end;  
