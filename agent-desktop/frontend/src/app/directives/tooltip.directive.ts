import { Directive, ElementRef, HostListener, Input, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appTooltip]',
  standalone: true
})
export class TooltipDirective {
  @Input('appTooltip') tooltipText: string = '';
  @Input() tooltipPosition: 'top' | 'bottom' | 'left' | 'right' = 'top';
  private tooltipElement: HTMLElement | null = null;

  constructor(private el: ElementRef, private renderer: Renderer2) {}

  @HostListener('mouseenter')
  onMouseEnter() {
    if (!this.tooltipText) return;
    this.showTooltip();
  }

  @HostListener('mouseleave')
  onMouseLeave() {
    this.hideTooltip();
  }

  private showTooltip() {
    this.tooltipElement = this.renderer.createElement('div');
    this.renderer.addClass(this.tooltipElement, 'tooltip');
    this.renderer.setProperty(this.tooltipElement, 'textContent', this.tooltipText);
    const hostPos = this.el.nativeElement.getBoundingClientRect();
    const pos = this.getPosition(hostPos);
    this.renderer.setStyle(this.tooltipElement, 'position', 'fixed');
    this.renderer.setStyle(this.tooltipElement, 'left', `${pos.left}px`);
    this.renderer.setStyle(this.tooltipElement, 'top', `${pos.top}px`);
    this.renderer.setStyle(this.tooltipElement, 'background', '#1e293b');
    this.renderer.setStyle(this.tooltipElement, 'color', '#fff');
    this.renderer.setStyle(this.tooltipElement, 'padding', '8px 12px');
    this.renderer.setStyle(this.tooltipElement, 'border-radius', '6px');
    this.renderer.setStyle(this.tooltipElement, 'font-size', '12px');
    this.renderer.setStyle(this.tooltipElement, 'z-index', '9999');
    this.renderer.setStyle(this.tooltipElement, 'white-space', 'nowrap');
    this.renderer.setStyle(this.tooltipElement, 'pointer-events', 'none');
    this.renderer.setStyle(this.tooltipElement, 'box-shadow', '0 4px 12px rgba(0,0,0,0.3)');
    this.renderer.appendChild(document.body, this.tooltipElement);
  }

  private hideTooltip() {
    if (this.tooltipElement) {
      this.renderer.removeChild(document.body, this.tooltipElement);
      this.tooltipElement = null;
    }
  }

  private getPosition(hostPos: DOMRect): { left: number; top: number } {
    const offset = 8;
    switch (this.tooltipPosition) {
      case 'bottom': return { left: hostPos.left + hostPos.width / 2, top: hostPos.bottom + offset };
      case 'left': return { left: hostPos.left - offset, top: hostPos.top + hostPos.height / 2 };
      case 'right': return { left: hostPos.right + offset, top: hostPos.top + hostPos.height / 2 };
      default: return { left: hostPos.left + hostPos.width / 2, top: hostPos.top - offset };
    }
  }
}
