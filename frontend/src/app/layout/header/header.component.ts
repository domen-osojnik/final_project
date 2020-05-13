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
            label: 'Dashboard',
            link: './dashboard',
            index: 1,
            
            image: 'commute'
        }, {
            label: 'Road information',
            link: './driveInfo',
            index: 2,
            image: 'track_changes'
        }, 
    ];
}

  ngOnInit(): void {
    this.router.events.subscribe((res) => {
      this.activeLinkIndex = this.navLinks.indexOf(this.navLinks.find(tab => tab.link === '.' + this.router.url));
  });
  }

}
