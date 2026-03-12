import CoreGraphics
import Foundation
import ImageIO
import UniformTypeIdentifiers

let size = 1024
let cs = CGColorSpace(name: CGColorSpace.sRGB)!
let ctx = CGContext(data: nil, width: size, height: size,
                   bitsPerComponent: 8, bytesPerRow: 0,
                   space: cs,
                   bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue)!

// Flip coords so (0,0) = top-left, consistent with the design
ctx.translateBy(x: 0, y: CGFloat(size))
ctx.scaleBy(x: 1, y: -1)

func rgb(_ r: CGFloat, _ g: CGFloat, _ b: CGFloat, _ a: CGFloat = 1) -> CGColor {
    CGColor(colorSpace: cs, components: [r/255, g/255, b/255, a])!
}

let indigo     = rgb(63, 81, 181)
let indigoLight = rgb(92, 107, 192)
let white      = rgb(255, 255, 255)
let amber      = rgb(255, 193, 7)

// ── Background (full square – iOS clips to rounded rect itself) ──────────────
ctx.setFillColor(indigo)
ctx.fill(CGRect(x: 0, y: 0, width: size, height: size))

// Lighter shimmer top half
let shimmerPath = CGMutablePath()
shimmerPath.addRect(CGRect(x: 0, y: 0, width: size, height: size / 2 + 40))
ctx.addPath(shimmerPath)
ctx.setFillColor(indigoLight)
ctx.fillPath()

// ── Shopping bag body ────────────────────────────────────────────────────────
func addRoundedRect(_ ctx: CGContext, rect: CGRect, radius: CGFloat) {
    let path = CGPath(roundedRect: rect, cornerWidth: radius, cornerHeight: radius, transform: nil)
    ctx.addPath(path)
}

let bagRect = CGRect(x: 260, y: 360, width: 504, height: 440)
addRoundedRect(ctx, rect: bagRect, radius: 50)
ctx.setFillColor(white)
ctx.fillPath()

// ── Bag handle (arc) ─────────────────────────────────────────────────────────
let handleCX = CGFloat(size) / 2
let handleCY = CGFloat(280)
let handleRX = CGFloat(145)
let handleRY = CGFloat(100)
let handleW  = CGFloat(46)

// Draw a thick arc by stroking an ellipse arc
ctx.saveGState()
ctx.setStrokeColor(white)
ctx.setLineWidth(handleW)
ctx.setLineCap(.round)

// Build ellipse arc path from 200° to 340° (top arch)
let startAngle = (200.0 * CGFloat.pi) / 180.0
let endAngle   = (340.0 * CGFloat.pi) / 180.0
ctx.move(to: CGPoint(x: handleCX + handleRX * cos(startAngle),
                     y: handleCY + handleRY * sin(startAngle)))
// Use a bezier approximation: draw arc by converting to bezier
let arcPath = CGMutablePath()
arcPath.addArc(center: CGPoint(x: handleCX, y: handleCY),
               radius: handleRX,
               startAngle: startAngle,
               endAngle: endAngle,
               clockwise: false)
// Scale Y to get ellipse
ctx.concatenate(CGAffineTransform(a: 1, b: 0, c: 0, d: handleRY/handleRX,
                                   tx: 0, ty: handleCY - handleCY*(handleRY/handleRX)))
ctx.addPath(arcPath)
ctx.strokePath()
ctx.restoreGState()

// ── Magnifying glass (inside bag) ────────────────────────────────────────────
let mgCX = CGFloat(size) / 2
let mgCY = CGFloat(555)
let mgROuter = CGFloat(115)
let mgRInner = CGFloat(80)
let mgHandleW = CGFloat(34)

// Outer white ring
ctx.setFillColor(white)
ctx.addEllipse(in: CGRect(x: mgCX - mgROuter, y: mgCY - mgROuter,
                           width: mgROuter*2, height: mgROuter*2))
ctx.fillPath()

// Inner indigo lens
ctx.setFillColor(indigo)
ctx.addEllipse(in: CGRect(x: mgCX - mgRInner, y: mgCY - mgRInner,
                           width: mgRInner*2, height: mgRInner*2))
ctx.fillPath()

// Handle (45-degree line, bottom-right)
let angle = CGFloat(45) * .pi / 180
let hx1 = mgCX + mgROuter * cos(angle)
let hy1 = mgCY + mgROuter * sin(angle)
let hx2 = mgCX + (mgROuter + 115) * cos(angle)
let hy2 = mgCY + (mgROuter + 115) * sin(angle)
ctx.setStrokeColor(white)
ctx.setLineWidth(mgHandleW)
ctx.setLineCap(.round)
ctx.move(to: CGPoint(x: hx1, y: hy1))
ctx.addLine(to: CGPoint(x: hx2, y: hy2))
ctx.strokePath()

// ── Amber price-tag badge (bottom-right of bag) ───────────────────────────────
let tagCX = CGFloat(680)
let tagCY = CGFloat(740)
let tagR  = CGFloat(58)
ctx.setFillColor(amber)
ctx.addEllipse(in: CGRect(x: tagCX - tagR, y: tagCY - tagR,
                           width: tagR*2, height: tagR*2))
ctx.fillPath()

// $ symbol inside badge
ctx.setStrokeColor(white)
ctx.setLineWidth(9)
ctx.setLineCap(.butt)
// vertical bar
ctx.move(to: CGPoint(x: tagCX, y: tagCY - 30))
ctx.addLine(to: CGPoint(x: tagCX, y: tagCY + 30))
ctx.strokePath()
// top arc
ctx.addArc(center: CGPoint(x: tagCX, y: tagCY - 12),
           radius: 22, startAngle: 0, endAngle: .pi, clockwise: false)
ctx.strokePath()
// bottom arc
ctx.addArc(center: CGPoint(x: tagCX, y: tagCY + 12),
           radius: 22, startAngle: .pi, endAngle: 2 * .pi, clockwise: false)
ctx.strokePath()

// ── Save as PNG ───────────────────────────────────────────────────────────────
let cgImage = ctx.makeImage()!
let url = URL(fileURLWithPath: "/Users/swamirangareddy/Documents/Personal Apps/Product Browser/iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png")
let dest = CGImageDestinationCreateWithURL(url as CFURL, UTType.png.identifier as CFString, 1, nil)!
CGImageDestinationAddImage(dest, cgImage, nil)
CGImageDestinationFinalize(dest)
print("Done: \(url.path)")

