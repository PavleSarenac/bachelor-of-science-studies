import { AfterViewInit, Component, OnInit } from '@angular/core';

declare var JitsiMeetExternalAPI: any

@Component({
  selector: 'app-jitsi',
  templateUrl: './jitsi.component.html',
  styleUrls: ['./jitsi.component.css']
})
export class JitsiComponent implements OnInit, AfterViewInit {
  domain: string = "meet.jit.si";
  room: any;
  options: any;
  api: any;
  user: any;

  isAudioMuted = false;
  isVideoMuted = false;

  userData: any

  constructor() { }

  ngOnInit(): void {
    this.userData = JSON.parse(localStorage.getItem("loggedInUser")!)
    this.room = "Class"
    this.user = {
      name: this.userData.username
    }
  }

  ngAfterViewInit(): void {
    this.options = {
      roomName: this.room,
      width: 900,
      height: 500,
      configOverwrite: { prejoinPageEnabled: false },
      interfaceConfigOverwrite: {},
      parentNode: document.querySelector('#jitsi-iframe'),
      userInfo: {
        displayName: this.user.name
      }
    }

    this.api = new JitsiMeetExternalAPI(this.domain, this.options);

    this.api.addEventListeners({
      readyToClose: this.handleClose,
      participantLeft: this.handleParticipantLeft,
      participantJoined: this.handleParticipantJoined,
      videoConferenceJoined: this.handleVideoConferenceJoined,
      videoConferenceLeft: this.handleVideoConferenceLeft,
      audioMuteStatusChanged: this.handleMuteStatus,
      videoMuteStatusChanged: this.handleVideoStatus
    });
  }

  handleClose = () => {
    console.log("handleClose");
  }

  handleParticipantLeft = async (participant: any) => {
    console.log("handleParticipantLeft", participant);
    const data = await this.getParticipants();
  }

  handleParticipantJoined = async (participant: any) => {
    console.log("handleParticipantJoined", participant);
    const data = await this.getParticipants();
  }

  handleVideoConferenceJoined = async (participant: any) => {
    console.log("handleVideoConferenceJoined", participant);
    const data = await this.getParticipants();
  }

  handleVideoConferenceLeft = () => {
    console.log("handleVideoConferenceLeft");
  }

  handleMuteStatus = (audio: any) => {
    console.log("handleMuteStatus", audio);
  }

  handleVideoStatus = (video: any) => {
    console.log("handleVideoStatus", video);
  }

  getParticipants() {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        resolve(this.api.getParticipantsInfo());
      }, 500)
    });
  }

  executeCommand(command: string) {
    this.api.executeCommand(command)
    if (command == 'hangup') {
      return
    }

    if (command == 'toggleAudio') {
      this.isAudioMuted = !this.isAudioMuted;
    }

    if (command == 'toggleVideo') {
      this.isVideoMuted = !this.isVideoMuted;
    }
  }

}
