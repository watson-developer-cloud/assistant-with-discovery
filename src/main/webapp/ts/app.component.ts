/**
 * (C) Copyright IBM Corp. 2016. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
import {Component} from 'angular2/core';
import {DialogResponse} from './dialog.response';
import {DialogService} from './dialog.service';
import {Http, Headers} from 'angular2/http';
import {CeDocComponent} from './ce.docs';
import {PayloadComponent} from './payload';

/*
 * Main entry point to the application. This component is responsible for the entire page layout.
 */
@Component ({
  directives: [CeDocComponent, PayloadComponent],
  providers: [DialogService],
  selector: 'chat-app',
  template: `
  <div id='view-change-button' class='button' (click)='togglePanel($event)'>
    <img title='Click to Collapse' class='option full' src='../img/Chat Button.png'>
    <img title='Click to Expand' class='option not-full' src='../img/Code Button.png'>
  </div>
  <div id='parent' class='parentDiv' (window:resize)='onResize($event)'>
      <div id='scrollingChat'>
        <div id='segments' *ngFor='#segment of segments'>
          <div [class]='segment.isUser() ? "from-user" :
            (segment !== segments[segments.length - 1] ? "from-watson" : "from-watson-latest")'>
            <p class='padding' [innerHtml]='segment.getText()'></p>
            <div *ngIf='!segment.isUser() && segment.getCe().length>0 && segment !== segments[0]'>
              <span title='Click to Collapse' (click)='CeToggle($event)' style='border : none;'
              class='expcoll'>Collapse Results <span class='sign'>-</span></span>
              <div class='toggleCe'>
                <ce-doc *ngFor='#doc of segment.getCe()' [doc]='doc'></ce-doc>
              </div>
            </div>
          </div>
          <div class='clear'></div>
          <div *ngIf='segment.isUser() && segment == segments[segments.length - 1]' class='load'></div>
        </div>
      </div>
      <div class='right'> <!-- Display the payload to/from Watson -->
        <div id='payload-column' class='fixed-column content-column'>
          <payload id='payload-request' class='payload' label='Sent to Watson' [style]='segments.length <= 2 ?
            "display : none;" : ""' [payload]='(segments.length > 1 && segments[segments.length - 2].isUser()) ?
            segments[segments.length - 2].getPayload() : null'></payload>
          <payload id='payload-response' class='payload' label='Watson understands' [style]='segments.length <= 1 ?
            "display : none;" : ""' [payload]='(segments.length > 0 && !segments[segments.length - 1].isUser()) ?
            segments[segments.length - 1].getPayload() : null'></payload>
        </div>
      </div>
      <footer>
      <label for='textInput' class='inputOutline'>
        <input id='textInput' class='input responsive-column' placeholder='Type something' type='text'
          [(ngModel)]='question' (keydown)='keypressed($event)' style='width:100%'>
      </label>
      <div class='draw'></div>
    </footer>
  </div>
    `
})
export class AppComponent {
  // Store the response so we can display the JSON for end user to see
  // We will also need to use the response's context for subsequent calls

  private response : any = null;
  private timer : any = null;
  private setupTimer : any = null;
  private question : string = null;
  private segments : DialogResponse[] = []; // Array of requests and responses
  private workspace_id : string = null;
  private langData : any;

  constructor (private _dialogService : DialogService, private http : Http) {
    this.getLang();
  }
/*
 * This method is responsible for detecting user locale and getting locale specific content to be displayed by making a
 * GET request to the respective file.
 */
  private getLang () {
    let browserLang = window.navigator.language || window.navigator.userLanguage;
    let complLang = browserLang.split('-');
    let lang    = complLang[0];
    let lang_url = 'locale/' + lang + '.json';
    this.http.get(lang_url).map(res => res.json()).subscribe(
      data =>  {
        this.langData = data;
        this.segments.push (new DialogResponse (
          this.langData.Description,
          false, null, null));
      },
      error => {
        let lang_url = 'locale/en.json';
        this.http.get(lang_url).map(res => res.json()).subscribe(
          data =>  {
            this.langData = data;
            this.segments.push (new DialogResponse (
              this.langData.Description,
            false, null, null));
          },
          error => alert (JSON.stringify (error)));
      });
  }
  private ngAfterViewInit (_dialogService : DialogService) {
    this.checkSetup(_dialogService);
    let rightColumn = <HTMLElement>document.querySelector ('.right');
    this.resizePayloadColumn (rightColumn);
  }
/*
 * This method is responsible for detecting if the set-up processs involving creation of various Watson services
 * and configuring them is complete. The status is checked every 5 seconds till its complete.
 * A loading screen is displayed to show set-up progress accordingly.
 */
  private checkSetup (_dialogService : DialogService) {
    this._dialogService.setup().subscribe (
      data => {
        this.workspace_id = data.WORKSPACE_ID;
        let setup_state = data.setup_state;
        let setup_status_msg = data.setup_status_message;
        let setup_phase = data.setup_phase;
        let setup_message = data.setup_message;
        let setup_step = data.setup_step;
        let setup = <HTMLElement>document.querySelector ('.setup');
        let setup_status = <HTMLElement>document.querySelector ('.setup-msg');
        let chat_app = <HTMLElement>document.querySelector ('chat-app');
        let setupLoader = <HTMLElement>document.querySelector ('.setup-loader');
        let setupPhase = <HTMLElement>document.querySelector ('.setup-phase');
        let setupPhaseMsg = <HTMLElement>document.querySelector ('.setup-phase-msg');
        let errorPhase = <HTMLElement>document.querySelector ('.error-phase');
        let errorPhaseMsg = <HTMLElement>document.querySelector ('.error-phase-msg');
        let circles = <HTMLElement>document.querySelector ('.circles');
        let gerror = <HTMLElement>document.querySelector ('.gerror');
        let werror = <HTMLElement>document.querySelector ('.werror');
        let activeCircle = <HTMLElement>document.querySelector ('.active-circle');
        let nactiveCircle = <HTMLElement>document.querySelector ('.non-active-circle');
        setup_status.innerHTML = setup_status_msg;
        if (setup_state === 'not_ready') {
          document.body.style.backgroundColor = 'darkgray';
          chat_app.style.opacity = '0.25';
          setup.style.display = 'block';
          setupPhase.innerHTML = setup_phase;
          setupPhaseMsg.innerHTML = setup_message;
          if (setup_step === '0') {
            errorPhase.innerHTML = setup_phase;
            errorPhaseMsg.innerHTML = setup_message;
            setupLoader.style.display = 'none';
            setupPhase.style.display = 'none';
            setupPhaseMsg.style.display = 'none';
            circles.style.display = 'none';
            if (setup_phase !== 'Error') {
              werror.style.display = 'block';
            } else {
              gerror.style.display = 'block';
            }
            errorPhase.style.display = 'block';
            errorPhaseMsg.style.display = 'block';
          } else {
            setupLoader.style.display = 'block';
            setupPhase.style.display = 'block';
            setupPhaseMsg.style.display = 'block';
            circles.style.display = 'block';
            gerror.style.display = 'none';
            werror.style.display = 'none';
            errorPhase.style.display = 'none';
            errorPhaseMsg.style.display = 'none';
          }
          if (setup_step === '2') {
            activeCircle.classList.remove ('active-circle');
            activeCircle.classList.add ('non-active-circle');
            nactiveCircle.classList.remove ('non-active-circle');
            nactiveCircle.classList.add ('active-circle');
          }
          this.setupTimer = setTimeout(() => {
            this.checkSetup(_dialogService);
          }, 5000);
        } else {
          let payload = {'input': {'text': ''}};
          let chatColumn = <HTMLElement>document.querySelector ('#scrollingChat');
          this.callConversationService (chatColumn, payload);
          document.body.style.backgroundColor = 'white';
          chat_app.style.opacity = '1';
          setup.style.display = 'none';
          if (this.setupTimer) {
            clearTimeout (this.setupTimer);
        }
        }
      },
      error => alert (JSON.stringify (error)));
  }
  private onResize (event) {
    let rightColumn = <HTMLElement>document.querySelector ('.right');
    this.resizePayloadColumn (rightColumn);
  }
/*
 * This method is responsible for toggling Expand/Collapse section of CE content.
 */
  private CeToggle (event) {
    let targetElement;
    if (event.srcElement) {
      targetElement = event.srcElement;
    } else {
      targetElement = event.target;
    }
    if (targetElement.className === 'sign') {
      targetElement = targetElement.parentElement;
    }
    if (targetElement.innerText.indexOf('Collapse') !== -1) {
      targetElement.innerHTML = this.langData.EResults + '<span class=sign>+</span>';
      targetElement.style.border = '';
      targetElement.title = this.langData.Expand;
    } else {
      targetElement.innerHTML = this.langData.CResults + '<span class=sign>-</span>';
      targetElement.style.border = 'none';
      targetElement.title = this.langData.Collapse;
    }
    let expcoll = <HTMLElement>targetElement.nextElementSibling;
    if (expcoll && (expcoll.style.display === 'block' || expcoll.style.display === '')) {
      expcoll.style.display = 'none';
    } else {
      expcoll.style.display = 'block';
    }
  }
/*
 * This method is responsible for triggering a request whenever a Enter key is pressed .
 */
  private keypressed (event) {
    let element = <HTMLElement>document.querySelector ('.draw');
    let nw = element.offsetWidth + 7;
    if (event && event.keyCode === 8) {
      nw = element.offsetWidth - 7;
    }
    if (nw > 360) {
      nw = 360;
    }
    element.style.width = String (nw + 'px');
    if (event && event.keyCode === 13) {
      this.sendData ();
      element.style.width = '0px';
    }
  }
/*
 * This method is responsible for changing the layout of payload section based on screen resolution.
 */
  private resizePayloadColumn (rightColumn) {
    if (window.innerWidth < 730) {
      rightColumn.classList.add ('no-show');
    } else if (window.innerWidth < 830) {
      rightColumn.classList.remove ('no-show');
      rightColumn.style.width = '340px';
    } else if (window.innerWidth < 860) {
      rightColumn.classList.remove ('no-show');
      rightColumn.style.width = '445px';
    } else if (window.innerWidth < 951) {
      rightColumn.classList.remove ('no-show');
      rightColumn.style.width = '395px';
    } else {
      rightColumn.classList.remove ('no-show');
      rightColumn.style.width = '445px';
    }
  }
/*
 * This method is responsible for toggling the payload section to full screen or fixed layout by
 * clicking the Code Expand/Collapse icons at the top right .
 */
  private togglePanel (event) {
    let payloadColumn = <HTMLElement>document.querySelector ('#payload-column');
    let toggleButton = <HTMLElement>document.querySelector ('#view-change-button');
    let rightColumn = <HTMLElement>document.querySelector ('.right');
    let element;
    if (event.srcElement) {
      element = event.srcElement;
    } else {
      element = event.target;
    }
    if (toggleButton.classList.contains ('full')) {
      toggleButton.classList.remove ('full');
      payloadColumn.classList.remove ('full');
      this.resizePayloadColumn (rightColumn);
    } else {
      rightColumn.classList.remove ('no-show');
      rightColumn.style.width = '100%';
      toggleButton.classList.add ('full');
      payloadColumn.classList.add ('full');
    }
  }
/*
 * This method is responsible for preparing the data to send and call the method for Conversation Service
 */
  private sendData () {
    let chatColumn = <HTMLElement>document.querySelector ('#scrollingChat');
    chatColumn.classList.add ('loading');
    let q = '';
    if (this.question != null) {
      q = this.question;
    }
    this.question = '';
    let context = null;
    if (this.response != null) {
      context = this.response.context;
      // we are going to delete the context variable 'callRetrieveAndRank' before
      // sending back to the Conversation service
      if (context && context.callRetrieveAndRank) {
        delete context.callRetrieveAndRank;
      }
    }
    let input = {'text': q};
    let payload = {input, context};
    // Add the user utterance to the list of chat segments
    this.segments.push (new DialogResponse (q, true, null, payload));
    // Call the method which calls the proxy for the message api
    this.callConversationService (chatColumn, payload);
  }

  /*
   * This method is responsible for making a request to Conversation service with the corresponding user query.
   */

  private callConversationService (chatColumn, payload) {
    let responseText = '';
    let ce : any = null;

    // Send the user utterance to dialog, also send previous context
    this._dialogService.message (this.workspace_id, payload).subscribe (
      data1 => {
        this.response = data1;
        if (data1) {
          if (data1.error) {
            responseText = data1.error;
            data1 = this.langData.NResponse;
          } else if (data1.output) {
            if (data1.output.CEPayload && data1.output.CEPayload.length > 0) {
              ce = data1.output.CEPayload;
              responseText = this.langData.Great;

              if (ce.length === 1 && ce[0].title === 'No results found') {
                responseText = this.langData.NoResult;
                ce = null;
              }
            } else if (data1.output.text) {
              if (data1.output.text.length >= 1) {
                responseText = data1.output.text.join('<br>');
              }
              // responseText  data1.output.text.length >= 1 && !data1.output.text[0] ?
              // data1.output.text.join(' ').trim() : data1.output.text[0]; // tslint:disable-line max-line-length
            }
          }
        }
        this.segments.push (new DialogResponse (responseText, false, ce, data1));
        chatColumn.classList.remove ('loading');
        if (this.timer) {
          clearTimeout (this.timer);
        }
        this.timer = setTimeout (function () {
         let messages = document.getElementById('scrollingChat').getElementsByClassName('clear');
         document.getElementById('scrollingChat').scrollTop = messages[messages.length - 1].offsetTop;
        }, 50);
        document.getElementById('textInput').focus();
      },
      error => {
        let serviceDownMsg = this.langData.Log;
        this.segments.push (new DialogResponse (serviceDownMsg, false, ce, this.langData.NResponse));
        chatColumn.classList.remove ('loading');
      });
  }
}
