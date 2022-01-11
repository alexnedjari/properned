;This file will be executed next to the application bundle image
;I.e. current directory will contain folder SoundLooper with application files
[Setup]
AppId={{application}}
AppName=SoundLooper
AppVersion=${version}
AppVerName=SoundLooper ${version}
AppPublisher=Alexandre NEDJARI
AppComments=SoundLooper
AppCopyright=Copyright (C) 2016
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
;DefaultDirName={localappdata}\SoundLooper
DefaultDirName={pf}\SoundLooper
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
OutputBaseFilename=SoundLooper-${version}
Compression=lzma
SolidCompression=yes
PrivilegesRequired=lowest
SetupIconFile=SoundLooper\SoundLooper.ico
UninstallDisplayIcon={app}\SoundLooper.ico
UninstallDisplayName=SoundLooper
WizardImageStretch=Yes
WizardImageFile=SoundLooper-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=
ChangesAssociations=Yes

[Languages]
;Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "french"; MessagesFile: "compiler:Languages\French.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}";

[Dirs]
Name: "{app}"; Permissions : users-modify

[Files]
Source: "SoundLooper\SoundLooper.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "SoundLooper\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\SoundLooper"; Filename: "{app}\SoundLooper.exe"; IconFilename: "{app}\SoundLooper.ico"; 
;Check: returnTrue()
Name: "{commondesktop}\SoundLooper"; Filename: "{app}\SoundLooper.exe";  IconFilename: "{app}\SoundLooper.ico"; 
;Check: returnFalse()


[Run]
;Filename: "{app}\SoundLooper.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\SoundLooper.exe"; Description: "{cm:LaunchProgram,SoundLooper}"; Flags: nowait postinstall skipifsilent; 
;Check: returnTrue()
;Filename: "{app}\SoundLooper.exe"; Parameters: "-install -svcName ""SoundLooper"" -svcDesc ""SoundLooper"" -mainExe ""SoundLooper.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\SoundLooper.exe "; Parameters: "-uninstall -svcName SoundLooper -stopOnUninstall"; 
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
