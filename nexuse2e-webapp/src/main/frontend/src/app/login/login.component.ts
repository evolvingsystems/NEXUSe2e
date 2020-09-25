import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
@Injectable({ providedIn: 'root' })
export class LoginComponent implements OnInit {
  private response;

  constructor(private http: HttpClient) {
  }

  testAjax() {
    return this.http.get('http://localhost:8080/nexuse2e_webapp_war/ajax/login').subscribe(
      (data) => {
        console.log(data);
      },
      () => {
        console.log('There was an error :(');
      }
    );
  }

  ngOnInit() {
    this.testAjax();
  }

}
