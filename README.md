# Calculadora Científica — projeto Android

Aplicativo Android nativo em Java, com tema escuro e funcionamento offline.

## Recursos incluídos
- Operações básicas, parênteses e precedência matemática
- Raiz quadrada e quadrado
- Seno, cosseno e tangente com alternância DEG/RAD
- Logaritmo (log) e logaritmo natural (ln)
- Constantes π e e, porcentagem e sinal
- Histórico de cálculos
- Conversor de temperatura, distância, peso e volume

## Gerar o APK automaticamente pelo GitHub

O projeto já inclui o workflow `.github/workflows/android.yml`.

1. Entre em https://github.com e crie uma conta, caso ainda não tenha.
2. Crie um repositório novo chamado `CalculadoraCientifica`.
3. Extraia este ZIP no computador.
4. Envie **todo o conteúdo da pasta** para o repositório (incluindo a pasta oculta `.github`).
   - Mais simples: no repositório, use **Add file > Upload files** e arraste os arquivos e pastas extraídos.
   - Confirme o envio em **Commit changes** na branch `main`.
5. Abra a aba **Actions** do repositório. O workflow “Compilar APK Android” será iniciado.
6. Quando aparecer como concluído (ícone verde), abra a execução e baixe o artefato **Calculadora-Cientifica-APK**.
7. Extraia o artefato ZIP baixado. Dentro estará `app-debug.apk`.

Também é possível iniciar a compilação manualmente em **Actions > Compilar APK Android > Run workflow**.

## Requisitos e observações
- O GitHub compila usando Java 17, Gradle 8.9 e Android SDK 35.
- O arquivo gerado é um APK de depuração, adequado para testes e instalação manual.
- Para instalar, talvez seja necessário autorizar “Instalar apps desconhecidos” no Android.
- O workflow não publica o APK publicamente como release; ele o disponibiliza como artefato da execução por 14 dias.
- Nenhuma conta ou credencial do GitHub é solicitada ou armazenada por este projeto.
