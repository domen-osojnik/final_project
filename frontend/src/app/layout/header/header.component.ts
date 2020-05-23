import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ThemePalette } from '@angular/material/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  navLinks: any[];
  activeLinkIndex = -1; 
  background: ThemePalette = "primary";

  constructor(private router: Router) {
    this.navLinks = [
        {
            label: 'Info',
            link: './info',
            index: 0,
            image: 'help_outline'
        }, {
            label: 'Stanje na cestah',
            link: './dashboard',
            index: 1,
            
            image: 'commute'
        }, {
            label: 'Podatki senzorjev',
            link: './driveInfo',
            index: 2,
            image: 'track_changes'
        } 
        , {
          label: 'Zaznavanje znakov',
          link: './signTracker',
          index: 3,
          image: 'directions'
      }, 
    ];
}

  ngOnInit(): void {
    this.router.events.subscribe((res) => {
      this.activeLinkIndex = this.navLinks.indexOf(this.navLinks.find(tab => tab.link === '.' + this.router.url));
  });
  }

}
