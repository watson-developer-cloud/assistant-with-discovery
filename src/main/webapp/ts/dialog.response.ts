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
 /*
 * This class is responsible for storing Dialog service class's response payload.
 */
export class DialogResponse {
  private user : boolean;
  private text : string;
  private ce : any;
  private arr : Array<any> = [];
  private payload : Object;

  constructor (text, user : boolean, ce, payload) {
    this.user = user;
    this.text = text;
    this.ce = ce;
    if (ce) {
      this.arr = [];
      for (let i = 0; i < ce.length; i++) {
        this.arr.push ({body : ce[i].body, confidence : ce[i].confidence,
          highlight : ce[i].highlight, sourceUrl : ce[i].sourceUrl, title : ce[i].title});
      }
    }
    this.payload = payload;
  }

  public getText () {
    return this.text;
  }

  public isUser () {
    return this.user;
  }

  public getCe () {
    return this.arr;
  }

  public getPayload () {
    return this.payload;
  }
}
