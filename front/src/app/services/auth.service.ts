import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, of, tap} from 'rxjs';
import { AuthResponse } from '../models/authResponse';  // Modèle de la réponse de l'API

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth'; // URL de ton API (assure-toi qu'elle soit correcte)

  constructor(private http: HttpClient) {}

  register(userData: { name: string; email: string; password: string }): Observable<AuthResponse> {
    const headers = new HttpHeaders(); // Créer des headers sans token
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData, { headers }).pipe(
      tap((response: AuthResponse) => {
        console.log('Inscription réussie', response);
        if (response && response.token) {
          localStorage.setItem('token', response.token);
        }
      }),
      catchError((error) => {
        console.error('Erreur lors de l\'inscription', error);
        // Retourne un objet AuthResponse vide en cas d'erreur
        return of({ token: '' }); // Retourne un AuthResponse avec token vide
      })
    );
  }
}
