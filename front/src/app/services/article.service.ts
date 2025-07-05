import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Article } from '../models/article';
import { AuthService } from './auth.service';  // Import AuthService pour accéder au token

@Injectable({
  providedIn: 'root',
})
export class ArticleService {

  private apiUrl = 'http://localhost:8080/api/articles';  // Utilisation de l'API

  constructor(private http: HttpClient, private authService: AuthService) {}

  // Méthode pour récupérer tous les articles
  getArticles(): Observable<Article[]> {
    const token = this.authService.getToken(); // Récupérer le token depuis le service AuthService

    const headers = new HttpHeaders({
      'Authorization': token ? `Bearer ${token}` : '', // Si le token existe, on l'ajoute dans l'en-tête
    });

    // Ajouter l'en-tête Authorization dans la requête
    return this.http.get<Article[]>(this.apiUrl, { headers });
  }
}
