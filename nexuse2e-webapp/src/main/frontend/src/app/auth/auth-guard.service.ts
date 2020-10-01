import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

  constructor(private http: HttpClient, private router: Router) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    return this.http.get('http://localhost:8080/auth/getUserName').pipe(
      catchError(e => {
        this.router.navigate(['/login'], { queryParams: { returnUrl: state.url }});
        return of(false);
      }),
      map(() => {
        return true;
      })
    );
  }
}
