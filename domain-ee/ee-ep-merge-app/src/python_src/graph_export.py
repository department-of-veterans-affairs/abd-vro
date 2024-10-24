from uuid import uuid4

import pydot
from schema.merge_job import MergeJob
from service.ep_merge_machine import EpMergeMachine

ERROR = '#f5d200'
SUCCESS = '#0ac28a'


class DotGraphMachine:
    font_name = 'Arial'
    state_font_size = '14pt'
    transition_font_size = '12pt'

    def __init__(self, machine):
        self.machine = machine

    def _get_graph(self):
        machine = self.machine
        return pydot.Dot(
            'list',
            graph_type='digraph',
            label=f'{machine.name} {machine.main_event}',
            fontname=self.font_name,
            fontsize=self.state_font_size,
            rankdir='TB',
        )

    def _initial_node(self):
        node = pydot.Node(
            'i',
            shape='circle',
            style='filled',
            fontsize='1pt',
            fixedsize='true',
            width=0.2,
            height=0.2,
        )
        node.set_fillcolor('black')
        return node

    def _final_node(self):
        node = pydot.Node(
            'f',
            shape='circle',
            style='invis',
            color='red',
            fontsize='1pt',
            fixedsize='true',
            width=0.2,
            height=0.2,
        )
        return node

    def _initial_edge(self):
        return pydot.Edge(
            'i',
            self.machine.initial_state.id,
            label='',
            color=SUCCESS,
            fontname=self.font_name,
            fontsize=self.transition_font_size,
        )

    def _state_as_node(self, state):
        if 'failed' in state.name or 'error' in state.name:
            color = ERROR
        elif 'Abort' in state.name:
            color = 'black'
        else:
            color = SUCCESS

        node = pydot.Node(
            state.id,
            label=f'{state.name.upper()}',
            shape='rectangle',
            style='rounded, filled',
            fontname=self.font_name,
            fontsize=self.state_font_size,
            peripheries=2 if state.final else 1,
            fillcolor='white',
            color=color,
        )

        return node

    def _transition_as_edge(self, transition):
        if self.machine.main_event not in transition.events:
            return None

        if 'failed' in transition.source.name or 'failed' in transition.target.name or 'error' in transition.target.name:
            color = ERROR
        elif 'Abort' in transition.source.name or 'Abort' in transition.target.name:
            color = 'black'
        else:
            color = SUCCESS

        return pydot.Edge(
            transition.source.id,
            transition.target.id,
            color=color,
            fontname=self.font_name,
            fontsize=self.transition_font_size,
        )

    def get_graph(self):
        graph = self._get_graph()
        graph.add_node(self._initial_node())
        graph.add_edge(self._initial_edge())

        all_edges = []
        for state in self.machine.states:
            node = self._state_as_node(state)
            if node:
                edges = []
                for transition in state.transitions:
                    if transition.internal:
                        continue
                    edge = self._transition_as_edge(transition)
                    if edge:
                        edges.append(edge)
                if edges:
                    graph.add_node(node)
                    for edge in edges:
                        graph.add_edge(edge)
                    all_edges.extend(edges)

        # Show only final nodes that have edges connected to them
        for state in [s for s in self.machine.states if s.final]:
            node = self._state_as_node(state)
            if [e for e in all_edges if state.id in [e.get_source(), e.get_destination()]]:
                graph.add_node(node)
                graph.add_edge(pydot.Edge(state.id, 'f', style='invis'))

        graph.add_node(self._final_node())
        return graph

    def __call__(self):
        return self.get_graph()


def generate_graph(main_event):
    job = MergeJob(job_id=uuid4(), pending_claim_id=1, ep400_claim_id=2)

    #
    graph = DotGraphMachine(EpMergeMachine(job, main_event))
    dot = graph()
    dot.write_png(f"EP_{main_event.replace('processing_from_running_','')}.png")


if __name__ == '__main__':
    events = [
        'process',
        'resume_processing_from_running_cancel_ep400_claim',
        'resume_processing_from_running_add_note_to_ep400_claim',
    ]
    for event in events:
        generate_graph(event)
