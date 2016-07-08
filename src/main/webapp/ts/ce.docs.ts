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

/*
 * This component is responsible for the CE Section layout under Expand/Collapse button.
 * Also contains the modal layout with detailed information
 */
@Component ({
  inputs: ['doc', 'body'],
  selector: 'ce-doc',
  template: `
  <div>
      <div title='View detailed Content' (click)='toggle(!isExpand)' class='docBody'>
        <div class='docBodyTitle' [innerHtml]='getTitle()'></div>
        <div class='docBodySnippet' [innerHtml]='getHighlighting()'></div>
      </div>
      <div class ='modal' [hidden]='isExpand'>
      <div class='modal-header'><div [innerHtml]='getTitle()' class='modal-doc'></div>
        <span class='modal-close' (click)='toggle(!isExpand)'>
        <img src='../img/close-button.png' class='close-button'></span>
      </div>
      <div class='modalDocTitle'>
        <a title='View content file' target='_blank' class='docLink' [href]='getSourceUrl()'>
          <div class='titleText'>Read the full document here</div>
        </a>
      </div>
      <div class='bodyText' [innerHtml]='getBody()'></div>
      <div class='disclaimer'>We’re demonstrating text limiting using only two paragraphs of the full article.
       You can turn this off for your own application.</div>
    </div>
    <div class='docTitle'>
      <a title='View content file' target='_blank' class='docLink' [href]='getSourceUrl()'>
        <div class='titleText'>Read the full document here</div>
      </a>
    </div>
  </div>
    `
})

export class CeDocComponent {
  private doc : any;
  private isExpand : boolean = true;
  private isLoading : boolean;
  private heighSet : boolean = false;

  private toggle (newval) {
    this.isExpand = newval;
  }

  private getTitle () {
    if (this.doc) {
      return this.doc.title;
    }
    return '';
  }

  private getSourceUrl () {
    if (this.doc) {
      return this.doc.sourceUrl;
    }
    return '';
  }
/*
 * Displays the Solr highlighted text with a special style.
 */
  private getHighlighting () {
    if (this.doc) {
      return this.doc.highlight.replace (/<em>/g, '<span class=highlighted>').replace (/<\/em>/g, '<\/span>');
    }
    return '';
  }

  private getBody () {
    if (this.doc) {
      return this.doc.body;
    }
    return '';
  }
}
