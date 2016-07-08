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
 * Displays the JSON payload from the user to Watson and from Watson to the client.
 */
@Component ({
  inputs: ['payload', 'label'],
  selector: 'payload',
  template: `
    <div>
        <div class='header-text'>{{ label }}</div>
        <div class='code-line responsive-columns-wrapper'>
        <pre class='line-numbers' [innerHtml]='createLineNumberString()'></pre>
        <pre class='payload-text responsive-column' [innerHtml]='getText()'></pre></div>
    </div>
    `
})
/*
 * This component is responsible for the Payload section layout inthe right hand side of the mian page.
 */
export class PayloadComponent {
  private payload : Object;

/*
 *Creates line numbers for payload section.
 */
  private createLineNumberString () {
    let numberOfLines = (this.getText ().match (/\n/g) || []).length + 1;
    if (numberOfLines === 1) {
      return '';
    }
    let lineString = '';
    let prefix = '';
    for (let i = 1; i <= numberOfLines; i++) {
      lineString += prefix;
      lineString += i;
      prefix = '\n';
    }
    return lineString;
  }

  private getText () {
    // This method will be invoked from the Angular 2.0 component template (above). The component receives a 'payload'
    // and a 'label' param when initialized. This method is responsible for converting the JSON to a syntax highlighted
    // text area.
    if (this.payload == null) {
      return '';
    }
    let convert : string = JSON.stringify (this.payload, null, 2);

    convert = convert.replace (/&/g, '&amp;').replace (/</g, '&lt;').replace (/>/g, '&gt;');
    convert = convert.replace (/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
      function (match) {
        let cls = 'number';
        if (/^"/.test (match)) {
          if (/:$/.test (match)) {
            cls = 'key';
          } else {
            cls = 'string';
          }
        } else if (/true|false/.test (match)) {
          cls = 'boolean';
        } else if (/null/.test (match)) {
          cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
    return convert;
  }
}
