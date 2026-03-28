pkgname=archangel-cli
pkgver=0.1.0
pkgrel=1
pkgdesc="AI-driven CLI assistant for Arch-based Linux systems"
arch=('x86_64')
url="https://github.com/Kernal-Penguins/ArchAngel---An-AI-driven-CLI-Assistant-for-arch-based-sytems"
license=('MIT')

depends=(
  'python'
)

makedepends=(
  'python-pip'
  'python-build'
  'python-installer'
  'python-wheel'
)

optdepends=(
'ollama: for AI features'
'jre-openjdk: required for backend service'
)

source=("$pkgname-$pkgver.tar.gz::$url/archive/refs/tags/v$pkgver.tar.gz")
sha256sums=('SKIP')

build() {
cd "$srcdir/ArchAngel---An-AI-driven-CLI-Assistant-for-arch-based-sytems-$pkgver"
python -m build --wheel --no-isolation
}

package() {
cd "$srcdir/ArchAngel---An-AI-driven-CLI-Assistant-for-arch-based-sytems-$pkgver"
python -m installer --destdir="$pkgdir" dist/*.whl
}
