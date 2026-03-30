# Code Smell: Long Parameter List — Análise com Qodana Quality Gate

**Aluno:** 93263 — Diogo Soares  
**Cheiro no código escolhido:** Long Parameter List (Martin Fowler)  
**Inspection ID (Qodana):** `ParametersPerMethod`  
**Projeto:** BattleShip2

---

## Contextualização

O cheiro no código *Long Parameter List*, catalogado por Martin Fowler, ocorre quando um método recebe um número excessivo de parâmetros, o que dificulta a leitura, compreensão e manutenção do código. Fowler recomenda a aplicação de refabricações como *Introduce Parameter Object*, *Preserve Whole Object* ou *Replace Parameter with Method Call* para reduzir a lista de parâmetros.

No livro *Object-Oriented Metrics in Practice* de Lanza e Marinescu, este cheiro é formalizado através da métrica **NOPM (Number of Parameters)**, com o limiar sugerido de **NOPM ≥ 4** para identificar métodos com listas de parâmetros potencialmente longas.

## Configuração do Quality Gate

Foi criado um workflow no GitHub Actions utilizando o **Qodana for JVM** (v2025.3) para detetar instâncias deste cheiro no projeto BattleShip2. O workflow está configurado no ficheiro YAML correspondente e executa uma análise estática completa do projeto.

A inspeção relevante do Qodana — `ParametersPerMethod` — utiliza por defeito um limiar de **mais de 5 parâmetros** (ou seja, ≥ 6), que é ligeiramente mais permissivo do que o valor de referência do livro (NOPM ≥ 4). Ainda assim, mesmo com este limiar mais conservador, o Qodana é capaz de detetar os casos mais críticos de listas de parâmetros longas.

## Resultados obtidos

A análise do Qodana ao projeto BattleShip2 **não detetou nenhuma instância** do cheiro *Long Parameter List*.

Isto significa que nenhum método no projeto possui mais de 5 parâmetros, o que indica que, no que diz respeito a este cheiro específico, o código do projeto já se encontra dentro dos limiares aceitáveis de qualidade.

## Interpretação

A ausência de ocorrências pode ser explicada pelos seguintes fatores:

1. **Dimensão e natureza do projeto** — O BattleShip2 é um projeto de âmbito académico com complexidade moderada. Os métodos implementados tendem a ter interfaces relativamente simples, sem necessidade de muitos parâmetros.

2. **Boas práticas de codificação** — Os membros do grupo seguiram, de forma geral, boas práticas ao estruturar os métodos, evitando naturalmente listas de parâmetros extensas.

3. **Limiar do Qodana ligeiramente mais alto** — O Qodana utiliza por defeito o limiar > 5 (ou seja, sinaliza a partir de 6 parâmetros), enquanto Lanza e Marinescu sugerem NOPM ≥ 4. Se o limiar fosse reduzido para ≥ 4, poderiam eventualmente surgir algumas ocorrências adicionais. No entanto, o plugin MetricsTree pode ser utilizado para verificar se existem métodos com exatamente 4 ou 5 parâmetros que, segundo o livro, já seriam considerados suspeitos.

## Conclusão

O quality gate para o cheiro *Long Parameter List* foi implementado com sucesso e executado sem erros no GitHub Actions. O facto de não terem sido detetadas instâncias é, neste caso, um resultado positivo que reflete a qualidade do código no que toca à dimensão das interfaces dos métodos. Para uma análise mais exigente e alinhada com os limiares de Lanza e Marinescu, recomenda-se complementar a análise do Qodana com os resultados do plugin MetricsTree (NOPM ≥ 4).
