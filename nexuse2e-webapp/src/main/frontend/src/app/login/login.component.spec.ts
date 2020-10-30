import {ComponentFixture, TestBed, waitForAsync} from '@angular/core/testing';
import {LoginComponent} from './login.component';
import {HttpClient, HttpHandler} from '@angular/common/http';
import {RouterTestingModule} from '@angular/router/testing';
import {ActivatedRoute, convertToParamMap, Router} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {of} from 'rxjs';

describe('LoginComponent (rendering)', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        FormsModule
      ],
      declarations: [LoginComponent],
      providers: [
        HttpClient,
        HttpHandler,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParams: convertToParamMap({
                returnUrl: ''
              })
            }
          }
        }
      ]
    });
    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  }));

  it('should render user and password fields', () => {
    const userField = fixture.nativeElement.querySelector('input[name="user"]');
    const passwordField = fixture.nativeElement.querySelector('input[name="password"]');
    expect(userField).toBeDefined();
    expect(passwordField).toBeDefined();
  });

  it('should not show an error message before the user has tried to login', () => {
    const loginError = fixture.nativeElement.querySelector('.login-error');
    expect(loginError).toBeFalsy();
  })

  it('should show an error message after a failed login attempt', () => {
    spyOn(component, 'login').and.throwError('login failed');
    fixture.nativeElement.querySelector('input[name="user"]').value = 'wrongUser';
    fixture.nativeElement.querySelector('input[name="password"]').value = 'wrongPassword';
    fixture.nativeElement.querySelector('.login-button').click();
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.login-error')).toBeTruthy();
  });
});

describe('LoginComponent (logic)', () => {
  let component: LoginComponent;
  let httpClient: HttpClient;
  let activatedRoute: ActivatedRoute;
  let router: Router;

  beforeEach(waitForAsync(() => {
    router = jasmine.createSpyObj('Router', {
      'navigateByUrl': function () {
      }
    });
    httpClient = new HttpClient(null);
    activatedRoute = new ActivatedRoute();
    component = new LoginComponent(httpClient, activatedRoute, router);
  }));

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should redirect after successful login', async () => {
    spyOn(httpClient, 'post').and.returnValue(of(true));
    await component.login({
      user: 'user',
      password: 'password'
    });
    expect(router.navigateByUrl).toHaveBeenCalled();
  })
});
