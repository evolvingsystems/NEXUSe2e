import { ComponentFixture, TestBed, waitForAsync } from "@angular/core/testing";
import { LoginComponent } from "./login.component";
import { RouterTestingModule } from "@angular/router/testing";
import { ActivatedRoute, convertToParamMap, Router } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { DataService } from "../services/data.service";
import { TranslateModule } from "@ngx-translate/core";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

describe("LoginComponent", () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let router: Router;
  let dataService: DataService;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        imports: [
          RouterTestingModule,
          FormsModule,
          HttpClientTestingModule,
          TranslateModule.forRoot(),
          MatFormFieldModule,
          MatInputModule,
          BrowserAnimationsModule,
        ],
        declarations: [LoginComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: {
              snapshot: {
                queryParams: convertToParamMap({
                  returnUrl: "",
                }),
              },
            },
          },
        ],
      });
      fixture = TestBed.createComponent(LoginComponent);
      component = fixture.componentInstance;
      router = TestBed.inject(Router);
      dataService = TestBed.inject(DataService);
      fixture.detectChanges();
    })
  );

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should render user and password fields", () => {
    const userField = fixture.nativeElement.querySelector('input[name="user"]');
    const passwordField = fixture.nativeElement.querySelector(
      'input[name="password"]'
    );
    expect(userField).toBeDefined();
    expect(passwordField).toBeDefined();
  });

  it("should not show an error message before the user has tried to login", () => {
    const loginError = fixture.nativeElement.querySelector(".login-error");
    expect(loginError).toBeFalsy();
  });

  it("should show an error message after a failed login attempt", () => {
    spyOn(component, "login").and.throwError("login failed");
    const userInput = fixture.nativeElement.querySelector('input[name="user"]');
    userInput.value = "wrongUser";
    userInput.dispatchEvent(new Event("input"));
    const passwordInput = fixture.nativeElement.querySelector(
      'input[name="password"]'
    );
    passwordInput.value = "wrongPassword";
    passwordInput.dispatchEvent(new Event("input"));
    const button = fixture.nativeElement.querySelector(".login-button");
    button.click();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector(".login-error")).toBeTruthy();
  });

  it("should show a warning if http is used", () => {
    component.isHttps = false;
    expect(fixture.nativeElement.querySelector(".http-warning")).toBeTruthy();
  });

  it("should redirect after successful login", async () => {
    spyOn(dataService, "postLogin");
    spyOn(router, "navigateByUrl");
    await component.login({
      user: "admin",
      password: "password",
    });

    expect(router.navigateByUrl).toHaveBeenCalled();
  });
});
