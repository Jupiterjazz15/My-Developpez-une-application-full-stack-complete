import { Component, OnInit } from '@angular/core';
import { ArticleService } from '../../services/article.service';
import { Article } from '../../models/article';

@Component({
  selector: 'app-article-list',
  templateUrl: './article-list.component.html',
  styleUrls: ['./article-list.component.scss']
})
export class ArticleListComponent implements OnInit {
  articles: Article[] = []; // Liste d'articles

  constructor(private articleService: ArticleService) {}

  ngOnInit(): void {

    this.articleService.getArticles().subscribe(   // Récupérer les articles depuis le service
      (response) => {
        this.articles = response;
      },
      (error) => {
        console.error('Erreur lors de la récupération des articles', error);
      }
    );
  }
}
