from langchain_google_genai import ChatGoogleGenerativeAI
from langchain_core.prompts import PromptTemplate
from langchain_core.LLMChain import LLMChain
from dotenv import load_dotenv

load_dotenv()

# Initialize LLM
model = ChatGoogleGenerativeAI(
    model="gemini-2.5-flash",
    temperature=0.4
)

# Create prompt template
candidate_prompt = PromptTemplate(
    input_variables=["fen"],
    template="""
You are a strong chess analyst.
Given this FEN position:

{fen}

Suggest exactly THREE legal candidate moves in UCI format.
Output ONLY a JSON list like:
["e2e4", "g1f3", "d2d4"]
"""
)

candidate_chain = LLMChain(llm=model, prompt=candidate_prompt)

# Format prompt with input
final_prompt = prompt.format(topic="Quantum Computing")

# Call model
response = model.invoke(final_prompt)

print(response.content)
