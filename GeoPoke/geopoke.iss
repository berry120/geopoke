; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "Geopoke"
#define MyAppVersion "1.0"
#define MyAppPublisher "Michael Berry"
#define MyAppURL "http://code.google.com/p/geopoke/"
#define MyAppExeName "Geopoke.exe"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{AC5C0948-E560-46F8-83B2-8EB26363724C}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\{#MyAppName}
DefaultGroupName={#MyAppName}
AllowNoIcons=yes
LicenseFile=licenses/gplv3.txt
OutputBaseFilename=setup
Compression=lzma
SolidCompression=yes
ChangesAssociations=yes

[Registry]
Root: HKCR; Subkey: ".geopl"; ValueType: string; ValueName: ""; ValueData: "Geopoke List"; Flags: uninsdeletevalue
Root: HKCR; Subkey: "Geopoke List"; ValueType: string; ValueName: ""; ValueData: "Geopoke List"; Flags: uninsdeletekey
Root: HKCR; Subkey: "Geopoke List\DefaultIcon"; ValueType: string; ValueName: ""; ValueData: "{app}\img\logo.ico,0"
Root: HKCR; Subkey: "Geopoke List\shell\open\command"; ValueType: string; ValueName: ""; ValueData: """{app}\Geopoke.exe"" ""%1"""

[Code]
function InitializeSetup(): Boolean;
var
  ErrorCode: Integer;
  JavaInstalled : Boolean;
  Result1 : Boolean;
begin
  JavaInstalled := RegKeyExists(HKLM,'SOFTWARE\JavaSoft\Java Runtime Environment\1.7');
  if IsWin64 then
  begin
    JavaInstalled := RegKeyExists(HKLM64,'SOFTWARE\JavaSoft\Java Runtime Environment\1.7');
  end;
  if JavaInstalled then
  begin
    Result := true;
  end else
    begin
      Result1 := MsgBox('This tool requires Java 7 to run. Please download and install the JRE and run this setup again. Do you want to download it now?', mbConfirmation, MB_YESNO) = idYes;
      if Result1 = false then
      begin
        Result:=false;
      end else
      begin
        Result:=false;
        ShellExec('open', 'http://www.oracle.com/technetwork/java/javase/downloads/index.html','','',SW_SHOWNORMAL,ewNoWait,ErrorCode);
      end;
    end;
  end;
end.

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"
Name: "quicklaunchicon"; Description: "{cm:CreateQuickLaunchIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "dist/Geopoke.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist/Geopoke.jar"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist/googlemap.html"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist/gpstemplate.html"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist/style.xslt"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist/bubble.png"; DestDir: "{app}"; Flags: ignoreversion
Source: "dist/lib\*"; DestDir: "{app}\lib"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "dist/img\*"; DestDir: "{app}\img"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\img\logo.ico"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; IconFilename: "{app}\img\logo.ico"; Tasks: desktopicon
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\{#MyAppName}"; IconFilename: "{app}\img\logo.ico"; Filename: "{app}\{#MyAppExeName}"; Tasks: quicklaunchicon

[Run]
Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, "&", "&&")}}"; Flags: shellexec postinstall skipifsilent