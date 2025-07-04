import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Récupérer le token depuis le localStorage
    const token = localStorage.getItem('token'); // Assure-toi que le token est bien stocké dans localStorage

    // Si un token existe et que la route n'est pas /register, on l'ajoute dans l'en-tête Authorization
    if (token && !req.url.includes('/register')) {
      const cloned = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`  // Ajouter le token dans l'en-tête
        }
      });

      return next.handle(cloned); // Continuer avec la requête modifiée
    }

    // Si aucun token ou si la route est "/register", on continue sans ajouter l'en-tête Authorization
    return next.handle(req);
  }
}
