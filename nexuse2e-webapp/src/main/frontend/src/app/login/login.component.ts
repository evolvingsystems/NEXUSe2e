import { Component, Injectable, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { DataService } from "../data/data.service";

interface LoginData {
  user: string;
  password: string;
}

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
})
@Injectable({ providedIn: "root" })
export class LoginComponent implements OnInit {
  returnUrl!: string;
  isHttps?: boolean;
  machineName?: string;
  loginError?: boolean;
  user?: string;
  password?: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService
  ) {
    this.dataService
      .get<string>("/machine-name")
      .then((name) => (this.machineName = name));
  }

  ngOnInit() {
    const returnUrl = "returnUrl";
    this.returnUrl = this.route.snapshot.queryParams[returnUrl] || "/";
    this.isHttps = location.protocol === "https";
  }

  async onSubmit() {
    if (this.user && this.password) {
      this.loginError = false;
      const loginData: LoginData = {
        user: this.user,
        password: this.password,
      };

      try {
        await this.login(loginData);
      } catch {
        this.loginError = true;
      }
    }
  }

  async login(loginData: LoginData) {
    await this.dataService.post("/login", loginData);
    await this.router.navigateByUrl(this.returnUrl);
  }
}
