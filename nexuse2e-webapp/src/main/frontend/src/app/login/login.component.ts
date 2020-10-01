import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
@Injectable({ providedIn: 'root' })
export class LoginComponent implements OnInit {
  private returnUrl: string;

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  testPost() {
    const testObject = {
      'user': 'admin',
      'password': 'admin'
    };
    const result = this.http.post('http://localhost:8080/auth', testObject);
    result.subscribe(
      () => {
        this.router.navigateByUrl(this.returnUrl);
        console.log('redirecting to', this.returnUrl);
      },
      () => {
        console.log('There was an error :(');
      }
    );

    return result;
  }

}
