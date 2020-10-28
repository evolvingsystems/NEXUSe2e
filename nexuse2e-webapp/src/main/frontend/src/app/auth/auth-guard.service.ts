import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private http: HttpClient, private router: Router) { }

  async canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
    try {
      await this.http.get(environment.API_URL + '/loggedIn').toPromise();
    } catch {
      await this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
      return false;
    }
    return true;
  }
}
