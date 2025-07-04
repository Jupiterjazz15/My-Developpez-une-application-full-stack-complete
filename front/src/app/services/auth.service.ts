import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {catchError, Observable, of, tap} from 'rxjs';
import { AuthResponse } from '../models/authResponse';  // Modèle de la réponse de l'API

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // URL de ton API (assure-toi qu'elle soit correcte)

  constructor(private http: HttpClient) {}

  // Inscription de l'utilisateur
  register(userData: { name: string; email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
      tap((response: AuthResponse) => {// Utilisation de `tap` pour effectuer une action après la réponse de l'API
        console.log('Voici le token -->', response.token)
        if (response && response.token) { // Enregistrer le token dans le localStorage si la réponse contient un token
          localStorage.setItem('token', response.token); // Stocker le token dans localStorage
        } else {
          console.error('Erreur : Aucune réponse de token');
        }
      }),
      catchError((error) => {
        console.error('Erreur lors de l\'inscription', error);
        return of({ token: '' }); // Retourne une observable vide ou une valeur par défaut si une erreur survient
      })
    );
  }

}
