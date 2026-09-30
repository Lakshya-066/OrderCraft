{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  buildInputs = with pkgs; [
    jdk25
    maven
  ];

  shellHook = ''
    echo "========================================"
    echo "   OrderCraft Development Environment   "
    echo "========================================"
    echo "Java version: $(java --version | head -n 1)"
    echo "Maven version: $(mvn --version | head -n 1)"
    echo ""
    echo "You can now run: cd ordercraft-backend && mvn spring-boot:run"
  '';
}
