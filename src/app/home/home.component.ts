import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth.service';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService
  ) {}

  navigate(parameter){
    console.log(this);
    this.router.navigate(['/viewable',{role: parameter}]);
  }

  ngOnInit(): void {
    if (this.router.url === '/home') {
      this.router.navigateByUrl(this.authService.getHomeRoute());
    }
  }

}
