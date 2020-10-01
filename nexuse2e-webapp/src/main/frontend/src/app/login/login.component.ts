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

  testGet() {
    return this.http.get('http://localhost:8080/ajax/login').subscribe(
      (data) => {
        console.log(data);
      },
      () => {
        console.log('There was an error :(');
      }
    );
  }

  testPost() {
    const testObject = {
      'user' : 'admin'
    };
    const result = this.http.post('http://localhost:8080/ajax/login', testObject);
    result.subscribe(
      (data) => {
        console.log(data);
      },
      () => {
        console.log('There was an error :(');
      }
    );

    return result;
  }

  ngOnInit() {
    this.testGet();
    this.testPost();
  }

}
