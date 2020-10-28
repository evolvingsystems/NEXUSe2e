import {Component, Injectable, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
@Injectable({ providedIn: 'root' })
export class LoginComponent implements OnInit {
  private returnUrl: string;
  isHttps: boolean;
  loginError: boolean;
  user: string;
  password: string;

  constructor(private http: HttpClient, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit() {
    const returnUrl = 'returnUrl';
    this.returnUrl = this.route.snapshot.queryParams[returnUrl] || '/';
    this.isHttps = location.protocol === 'https';
  }

  async login() {
    this.loginError = false;

    const loginData = {
      user: this.user,
      password: this.password
    };

    try {
      await this.http.post(environment.API_URL + '/login', loginData).toPromise();
      await this.router.navigateByUrl(this.returnUrl);
    } catch {
      this.loginError = true;
    }
  }

}
