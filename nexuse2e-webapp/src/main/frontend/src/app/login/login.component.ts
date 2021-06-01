import { Component, Injectable, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { DataService } from "../services/data.service";
import { NavigationService } from "../services/navigation.service";
import { PermissionService } from "../services/permission.service";
import { LoginData } from "../types";

@Component({
  selector: "app-login",
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.scss"],
})
@Injectable({ providedIn: "root" })
export class LoginComponent implements OnInit {
  isHttps?: boolean;
  machineName?: string;
  loginError?: boolean;
  user?: string;
  password?: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private navigationService: NavigationService,
    private permissionService: PermissionService
  ) {}

  async ngOnInit() {
    if (await this.dataService.isLoggedIn()) {
      this.router.navigate(["/dashboard"]);
    } else {
      this.isHttps = location.protocol === "https:";
      this.machineName = await this.dataService.getMachineName();
    }
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
    await this.dataService.postLogin(loginData);
    this.permissionService.updatePermissions();
    this.navigationService.hideNavigation();
    const returnUrl = this.route.snapshot.queryParams["returnUrl"] || "/";
    await this.router.navigateByUrl(returnUrl);
  }
}
