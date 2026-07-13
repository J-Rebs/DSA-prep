import React, { useState } from 'react';
import { 
  Play, 
  ArrowRight, 
  ArrowLeft, 
  RotateCcw, 
  Award, 
  CheckCircle2, 
  AlertCircle, 
  Cpu, 
  Code2, 
  BookOpen, 
  HelpCircle,
  Activity,
  Layers,
  Network
} from 'lucide-react';

// --- DATA DEFINITIONS ---
import INITIAL_CURRICULUM from './curriculum.json';

export default function App() {
  const [activeTab, setActiveTab] = useState('visualizer');
  const [selectedConcept, setSelectedConcept] = useState('binary_search');
  const [curriculum, setCurriculum] = useState(INITIAL_CURRICULUM);
  
  // Visualizer states
  const [visType, setVisType] = useState('binary_search'); // binary_search, list_reversal, list_cycle
  const [searchTarget, setSearchTarget] = useState(55);
  
  // Binary Search State
  const bsKeys = [10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120, 130, 140, 150, 160];
  const bsOffsets = [100, 200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200, 1300, 1400, 1500, 1600];
  
  const [bsState, setBsState] = useState({
    low: 0,
    high: bsKeys.length - 1,
    mid: -1,
    ans: -1,
    step: 0,
    log: "Set target key and click 'Step Forward' to trace.",
    complete: false
  });

  // Linked List Reversal State
  const initialNodes = [
    { id: 1, val: 10, nextId: 2, isReversed: false },
    { id: 2, val: 20, nextId: 3, isReversed: false },
    { id: 3, val: 30, nextId: 4, isReversed: false },
    { id: 4, val: 40, nextId: 5, isReversed: false },
    { id: 5, val: 50, nextId: null, isReversed: false }
  ];
  const [listState, setListState] = useState({
    nodes: initialNodes,
    prev: null,
    curr: 1,
    nextTemp: null,
    step: 0,
    log: "Initial state: prev = null, curr = Node(10)",
    complete: false
  });

  // Linked List Cycle State
  const [cycleState, setCycleState] = useState({
    slow: 1,
    fast: 1,
    step: 0,
    log: "Pointers initialized at Head: slow = Node(1), fast = Node(1)",
    complete: false
  });

  // --- LOGIC: BINARY SEARCH LOWER BOUND STEPPING ---
  const stepBinarySearch = () => {
    if (bsState.complete) return;
    
    let { low, high, mid, ans, step } = bsState;
    let log = "";
    let complete = false;

    if (low <= high) {
      mid = Math.floor((low + high) / 2);
      const midVal = bsKeys[mid];
      
      if (midVal <= searchTarget) {
        ans = mid;
        low = mid + 1;
        log = `Step ${step + 1}: keys[mid=${mid}] (${midVal}) <= target (${searchTarget}). Cache offset=${bsOffsets[mid]} and search right (low = mid + 1 = ${low})`;
      } else {
        high = mid - 1;
        log = `Step ${step + 1}: keys[mid=${mid}] (${midVal}) > target (${searchTarget}). Search left (high = mid - 1 = ${high})`;
      }
      step++;
    } else {
      complete = true;
      log = `Search complete! Resulting block offset is ${ans !== -1 ? bsOffsets[ans] : -1} (Key: ${ans !== -1 ? bsKeys[ans] : "None"})`;
    }

    setBsState({ low, high, mid, ans, step, log, complete });
  };

  const resetBinarySearch = () => {
    setBsState({
      low: 0,
      high: bsKeys.length - 1,
      mid: -1,
      ans: -1,
      step: 0,
      log: "Set target key and click 'Step Forward' to trace.",
      complete: false
    });
  };

  // --- LOGIC: LINKED LIST REVERSAL STEPPING ---
  const stepListReversal = () => {
    if (listState.complete) return;

    let { nodes, prev, curr, nextTemp, step } = listState;
    let log = "";
    let complete = false;

    if (curr === null) {
      complete = true;
      log = `Reversal complete! New head node is Node(50)`;
      setListState(prev => ({ ...prev, complete, log }));
      return;
    }

    const subStep = step % 4;
    const currentNode = nodes.find(n => n.id === curr);

    if (subStep === 0) {
      // 1. nextTemp = curr.next
      nextTemp = currentNode.nextId;
      log = `Pointer Step: nextTemp = curr.next (nextTemp -> Node(${nextTemp ? nodes.find(n => n.id === nextTemp).val : "null"}))`;
    } else if (subStep === 1) {
      // 2. curr.next = prev
      nodes = nodes.map(n => n.id === curr ? { ...n, nextId: prev, isReversed: true } : n);
      log = `Link Mutated: curr.next = prev (Inverting Node(${currentNode.val}) -> ${prev ? nodes.find(n => n.id === prev).val : "null"})`;
    } else if (subStep === 2) {
      // 3. prev = curr
      prev = curr;
      log = `Pointer Shift: prev = curr (prev -> Node(${currentNode.val}))`;
    } else if (subStep === 3) {
      // 4. curr = nextTemp
      curr = nextTemp;
      log = `Pointer Shift: curr = nextTemp (curr -> ${curr ? "Node(" + nodes.find(n => n.id === curr).val + ")" : "null"})`;
    }

    setListState({
      nodes,
      prev,
      curr,
      nextTemp,
      step: step + 1,
      log,
      complete
    });
  };

  const resetListReversal = () => {
    setListState({
      nodes: initialNodes,
      prev: null,
      curr: 1,
      nextTemp: null,
      step: 0,
      log: "Initial state: prev = null, curr = Node(10)",
      complete: false
    });
  };

  // --- LOGIC: FLOYD CYCLE RUNNER ---
  const stepCycleDetection = () => {
    if (cycleState.complete) return;

    let { slow, fast, step } = cycleState;
    let log = "";
    let complete = false;

    // Cycle paths: 1 -> 2 -> 3 -> 4 -> 5 -> 3 (loop back)
    const getNext = (nodeId) => {
      if (nodeId === 5) return 3;
      return nodeId + 1;
    };

    const nextSlow = getNext(slow);
    const nextFast = getNext(getNext(fast));
    step++;

    if (nextSlow === nextFast) {
      complete = true;
      log = `Cycle detected! slow and fast met at Node(${nextSlow})`;
    } else {
      log = `Step ${step}: slow moved to Node(${nextSlow}), fast moved to Node(${nextFast})`;
    }

    setCycleState({
      slow: nextSlow,
      fast: nextFast,
      step,
      log,
      complete
    });
  };

  const resetCycleDetection = () => {
    setCycleState({
      slow: 1,
      fast: 1,
      step: 0,
      log: "Pointers initialized at Head: slow = Node(1), fast = Node(1)",
      complete: false
    });
  };

  // Toggle problem checklist statuses
  const toggleProblemStatus = (conceptId, problemIndex) => {
    setCurriculum(prev => prev.map(phase => ({
      ...phase,
      concepts: phase.concepts.map(concept => {
        if (concept.id === conceptId && concept.problems) {
          const updatedProblems = [...concept.problems];
          const currentStatus = updatedProblems[problemIndex].status;
          let nextStatus = "🛑 Todo";
          if (currentStatus === "🛑 Todo") nextStatus = "🔄 In Progress";
          else if (currentStatus === "🔄 In Progress") nextStatus = "🟢 Solved";
          
          updatedProblems[problemIndex] = {
            ...updatedProblems[problemIndex],
            status: nextStatus
          };
          return { ...concept, problems: updatedProblems };
        }
        return concept;
      })
    })));
  };

  return (
    <div style={{ maxWidth: '1280px', margin: '0 auto', padding: '30px 20px' }}>
      
      {/* HEADER SECTION */}
      <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '40px' }} className="glass-panel" style={{ padding: '24px', borderRadius: '16px', border: '1px solid var(--border-color)', marginBottom: '30px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Activity size={28} color="var(--accent-indigo)" />
            <h1 style={{ fontSize: '1.8rem', fontWeight: '700', letterSpacing: '-0.5px' }}>DSA & Systems Study Engine</h1>
          </div>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', marginTop: '4px' }}>
            Visualizing pointer mechanics and mapping code algorithms to enterprise components.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '10px' }}>
          <button className={`nav-tab ${activeTab === 'visualizer' ? 'active' : ''}`} onClick={() => setActiveTab('visualizer')}>
            <Play size={18} /> Visualizers
          </button>
          <button className={`nav-tab ${activeTab === 'curriculum' ? 'active' : ''}`} onClick={() => setActiveTab('curriculum')}>
            <Layers size={18} /> Curriculum
          </button>
          <button className={`nav-tab ${activeTab === 'cheatsheet' ? 'active' : ''}`} onClick={() => setActiveTab('cheatsheet')}>
            <Cpu size={18} /> Java Optimization
          </button>
        </div>
      </header>

      {/* TABS CONTAINER */}
      <main>
        
        {/* 1. VISUALIZER TAB */}
        {activeTab === 'visualizer' && (
          <div style={{ display: 'grid', gridTemplateColumns: '300px 18fr', gap: '24px' }}>
            
            {/* Left selector */}
            <div className="glass-panel" style={{ padding: '20px', display: 'flex', flexDirection: 'column', gap: '12px', height: 'fit-content' }}>
              <h3 style={{ fontSize: '1rem', fontWeight: '600', color: 'var(--text-secondary)', marginBottom: '8px' }}>Select Pattern</h3>
              
              <button 
                className={`nav-tab ${visType === 'binary_search' ? 'active' : ''}`} 
                style={{ width: '100%', justifyContent: 'flex-start' }}
                onClick={() => { setVisType('binary_search'); resetBinarySearch(); }}
              >
                <Layers size={16} /> Binary Search
              </button>
              
              <button 
                className={`nav-tab ${visType === 'list_reversal' ? 'active' : ''}`} 
                style={{ width: '100%', justifyContent: 'flex-start' }}
                onClick={() => { setVisType('list_reversal'); resetListReversal(); }}
              >
                <Network size={16} /> List Reversal
              </button>

              <button 
                className={`nav-tab ${visType === 'list_cycle' ? 'active' : ''}`} 
                style={{ width: '100%', justifyContent: 'flex-start' }}
                onClick={() => { setVisType('list_cycle'); resetCycleDetection(); }}
              >
                <Activity size={16} /> Floyd's Cycle Detection
              </button>
            </div>

            {/* Right main canvas */}
            <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
              
              {/* BINARY SEARCH VISUALIZER */}
              {visType === 'binary_search' && (
                <div>
                  <div style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '16px', marginBottom: '20px' }}>
                    <h2 style={{ fontSize: '1.4rem', fontWeight: '600' }}>Binary Search: Lower Bound Range Finder</h2>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '4px' }}>
                      Maps to **SSTable Index Lookup**. Searches the greatest key &le; target to resolve the file block offset on disk.
                    </p>
                  </div>

                  {/* Input variables */}
                  <div style={{ display: 'flex', gap: '16px', alignItems: 'center', marginBottom: '24px' }}>
                    <label style={{ fontSize: '0.9rem', fontWeight: '500', color: 'var(--text-secondary)' }}>Target Query Key:</label>
                    <input 
                      type="number" 
                      value={searchTarget} 
                      onChange={(e) => { setSearchTarget(Number(e.target.value)); resetBinarySearch(); }}
                      style={{ background: 'rgba(255,255,255,0.05)', border: '1px solid var(--border-color)', padding: '8px 12px', borderRadius: '6px', color: '#fff', width: '80px', fontFamily: 'JetBrains Mono, monospace' }}
                    />
                    <button className="btn-primary" onClick={stepBinarySearch} disabled={bsState.complete}>
                      Step Forward <ArrowRight size={16} />
                    </button>
                    <button className="btn-secondary" onClick={resetBinarySearch}>
                      <RotateCcw size={16} /> Reset
                    </button>
                  </div>

                  {/* Display elements */}
                  <div className="array-container" style={{ background: 'rgba(0,0,0,0.2)', borderRadius: '12px', marginBottom: '24px' }}>
                    {bsKeys.map((key, idx) => {
                      const isLow = idx === bsState.low;
                      const isHigh = idx === bsState.high;
                      const isMid = idx === bsState.mid;
                      const isAns = idx === bsState.ans;
                      
                      let boxClass = "array-box";
                      if (isMid) boxClass += " mid";
                      else if (isAns) boxClass += " selected";
                      else if (idx >= bsState.low && idx <= bsState.high) boxClass += " active";
                      else boxClass += " out-of-bounds";

                      return (
                        <div key={idx} className={boxClass}>
                          <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', position: 'absolute', bottom: '-20px' }}>i={idx}</span>
                          <span style={{ fontSize: '0.95rem' }}>{key}</span>
                          
                          {/* Pointer tags */}
                          {isLow && <span className="pointer-label pointer-low">low</span>}
                          {isHigh && <span className="pointer-label pointer-high" style={{ top: isLow ? '-45px' : '-25px' }}>high</span>}
                          {isMid && <span className="pointer-label pointer-mid" style={{ top: (isLow || isHigh) ? '-45px' : '-25px' }}>mid</span>}
                        </div>
                      );
                    })}
                  </div>

                  {/* Details and state metrics */}
                  <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px', marginTop: '30px' }}>
                    <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
                      <h4 style={{ fontSize: '0.95rem', fontWeight: '600', color: 'var(--text-secondary)', marginBottom: '8px' }}>Execution Trace Log</h4>
                      <p style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: '0.9rem', color: 'var(--accent-cyan)' }}>{bsState.log}</p>
                    </div>
                    <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-color)', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                      <h4 style={{ fontSize: '0.95rem', fontWeight: '600', color: 'var(--text-secondary)', marginBottom: '4px' }}>Loop Variables</h4>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.85rem' }}>
                        <span>low index:</span> <span style={{ color: 'var(--accent-cyan)' }}>{bsState.low}</span>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.85rem' }}>
                        <span>high index:</span> <span style={{ color: 'var(--accent-rose)' }}>{bsState.high}</span>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.85rem' }}>
                        <span>mid index:</span> <span style={{ color: 'var(--accent-amber)' }}>{bsState.mid !== -1 ? bsState.mid : "N/A"}</span>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.85rem' }}>
                        <span>cached ans:</span> <span style={{ color: 'var(--accent-emerald)' }}>{bsState.ans !== -1 ? `index ${bsState.ans} (offset ${bsOffsets[bsState.ans]})` : "None"}</span>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* LINKED LIST REVERSAL */}
              {visType === 'list_reversal' && (
                <div>
                  <div style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '16px', marginBottom: '20px' }}>
                    <h2 style={{ fontSize: '1.4rem', fontWeight: '600' }}>Linked List: In-Place Node Reversal</h2>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '4px' }}>
                      Master pointer swaps. This visualizes shifting node next references using three variables (`prev`, `curr`, `nextTemp`) in place.
                    </p>
                  </div>

                  {/* Actions */}
                  <div style={{ display: 'flex', gap: '16px', alignItems: 'center', marginBottom: '24px' }}>
                    <button className="btn-primary" onClick={stepListReversal} disabled={listState.complete}>
                      Step Mutation <ArrowRight size={16} />
                    </button>
                    <button className="btn-secondary" onClick={resetListReversal}>
                      <RotateCcw size={16} /> Reset
                    </button>
                  </div>

                  {/* List visual nodes */}
                  <div className="list-container" style={{ background: 'rgba(0,0,0,0.2)', borderRadius: '12px', marginBottom: '24px' }}>
                    {listState.nodes.map((node, index) => {
                      const isCurr = node.id === listState.curr;
                      const isPrev = node.id === listState.prev;
                      const isNextTemp = node.id === listState.nextTemp;
                      
                      let nodeClass = "list-node";
                      if (isCurr) nodeClass += " curr";
                      else if (isPrev) nodeClass += " prev";

                      return (
                        <React.Fragment key={node.id}>
                          <div className={nodeClass}>
                            <span style={{ fontSize: '0.9rem' }}>{node.val}</span>
                            
                            {/* Tags */}
                            {isCurr && <span className="pointer-label pointer-low" style={{ background: 'var(--accent-indigo)', color: '#fff' }}>curr</span>}
                            {isPrev && <span className="pointer-label pointer-low" style={{ background: 'var(--accent-violet)', color: '#fff' }}>prev</span>}
                            {isNextTemp && <span className="pointer-label pointer-low" style={{ background: 'var(--accent-amber)', color: '#000', top: '-45px' }}>nextTemp</span>}
                          </div>
                          
                          {/* Rendering arrows representing next-references */}
                          {index < listState.nodes.length - 1 && (
                            <span className={`list-arrow ${node.isReversed ? 'reversed' : ''}`}>
                              &rarr;
                            </span>
                          )}
                        </React.Fragment>
                      );
                    })}
                    <span style={{ color: 'var(--text-muted)', fontFamily: 'JetBrains Mono, monospace' }}>NULL</span>
                  </div>

                  {/* Trace details */}
                  <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px', marginTop: '30px' }}>
                    <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
                      <h4 style={{ fontSize: '0.95rem', fontWeight: '600', color: 'var(--text-secondary)', marginBottom: '8px' }}>Pointer Mutation Log</h4>
                      <p style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: '0.9rem', color: 'var(--accent-violet)' }}>{listState.log}</p>
                    </div>
                    <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-color)', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                      <h4 style={{ fontSize: '0.95rem', fontWeight: '600', color: 'var(--text-secondary)', marginBottom: '4px' }}>Pointer Variables</h4>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.85rem' }}>
                        <span>prev:</span> <span style={{ color: 'var(--accent-violet)' }}>{listState.prev ? `Node(${listState.prev * 10})` : "null"}</span>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.85rem' }}>
                        <span>curr:</span> <span style={{ color: 'var(--accent-indigo)' }}>{listState.curr ? `Node(${listState.curr * 10})` : "null"}</span>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: '0.85rem' }}>
                        <span>nextTemp:</span> <span style={{ color: 'var(--accent-amber)' }}>{listState.nextTemp ? `Node(${listState.nextTemp * 10})` : "null"}</span>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {/* FLOYD CYCLE DETECTION */}
              {visType === 'list_cycle' && (
                <div>
                  <div style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '16px', marginBottom: '20px' }}>
                    <h2 style={{ fontSize: '1.4rem', fontWeight: '600' }}>Floyd's Cycle Detection (Pointer Loop Runner)</h2>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginTop: '4px' }}>
                      Visualizes `slow` and `fast` pointers traversing a looped list. Fast moves 2 steps, slow moves 1.
                    </p>
                  </div>

                  {/* Actions */}
                  <div style={{ display: 'flex', gap: '16px', alignItems: 'center', marginBottom: '24px' }}>
                    <button className="btn-primary" onClick={stepCycleDetection} disabled={cycleState.complete}>
                      Step Pointers <ArrowRight size={16} />
                    </button>
                    <button className="btn-secondary" onClick={resetCycleDetection}>
                      <RotateCcw size={16} /> Reset
                    </button>
                  </div>

                  {/* Loop Diagram */}
                  <div className="list-container" style={{ background: 'rgba(0,0,0,0.2)', borderRadius: '12px', marginBottom: '24px', flexWrap: 'wrap' }}>
                    {[1, 2, 3, 4, 5].map((id, index) => {
                      const isSlow = id === cycleState.slow;
                      const isFast = id === cycleState.fast;
                      
                      let nodeClass = "list-node";
                      if (isSlow) nodeClass += " slow";
                      if (isFast) nodeClass += " fast";

                      return (
                        <React.Fragment key={id}>
                          <div className={nodeClass}>
                            <span style={{ fontSize: '0.9rem' }}>N({id})</span>
                            
                            {/* Pointer markers */}
                            {isSlow && <span className="pointer-label pointer-low" style={{ background: 'var(--accent-cyan)', color: '#000' }}>slow</span>}
                            {isFast && <span className="pointer-label pointer-low" style={{ background: 'var(--accent-rose)', color: '#fff', top: isSlow ? '-45px' : '-25px' }}>fast</span>}
                          </div>
                          {index < 4 ? (
                            <span className="list-arrow">&rarr;</span>
                          ) : (
                            <span className="list-arrow" style={{ color: 'var(--accent-rose)' }}>&larr; (Loop back to N3)</span>
                          )}
                        </React.Fragment>
                      );
                    })}
                  </div>

                  {/* Log info */}
                  <div style={{ background: 'rgba(255,255,255,0.02)', padding: '16px', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
                    <h4 style={{ fontSize: '0.95rem', fontWeight: '600', color: 'var(--text-secondary)', marginBottom: '8px' }}>Pointer Positions</h4>
                    <p style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: '0.9rem', color: 'var(--accent-cyan)' }}>{cycleState.log}</p>
                  </div>
                </div>
              )}

            </div>
          </div>
        )}

        {/* 2. CURRICULUM PROGRESS TAB */}
        {activeTab === 'curriculum' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
            
            {/* Progress list by Phase */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <h2 style={{ fontSize: '1.5rem', fontWeight: '600' }}>Curriculum Mappings & Progress</h2>
              {curriculum.map((phase, phaseIdx) => (
                <div key={phaseIdx} className="glass-panel" style={{ padding: '20px' }}>
                  <div style={{ borderLeft: `4px solid ${phase.color}`, paddingLeft: '12px', marginBottom: '16px' }}>
                    <h3 style={{ fontSize: '1.1rem', fontWeight: '700' }}>{phase.phase}</h3>
                    <span style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>{phase.subtitle}</span>
                  </div>

                  <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                    {phase.concepts.map((concept, conceptIdx) => (
                      <div 
                        key={conceptIdx} 
                        onClick={() => setSelectedConcept(concept.id)}
                        style={{ 
                          padding: '12px 16px', 
                          borderRadius: '8px', 
                          background: selectedConcept === concept.id ? 'rgba(255,255,255,0.04)' : 'transparent',
                          border: selectedConcept === concept.id ? '1px solid rgba(99, 102, 241, 0.3)' : '1px solid transparent',
                          cursor: 'pointer',
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center'
                        }}
                      >
                        <div>
                          <h4 style={{ fontSize: '0.95rem', fontWeight: '600' }}>{concept.name}</h4>
                          <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>{concept.component}</p>
                        </div>
                        <span style={{ 
                          fontSize: '0.8rem', 
                          padding: '3px 8px', 
                          borderRadius: '4px',
                          background: concept.status === '🟢 Mastered' ? 'rgba(16,185,129,0.15)' : concept.status === '🔄 In Progress' ? 'rgba(245,158,11,0.15)' : 'rgba(255,255,255,0.05)',
                          color: concept.status === '🟢 Mastered' ? 'var(--accent-emerald)' : concept.status === '🔄 In Progress' ? 'var(--accent-amber)' : 'var(--text-secondary)'
                        }}>
                          {concept.status}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>

            {/* Selected concept 9-problem mastery ladder */}
            <div className="glass-panel" style={{ padding: '24px', height: 'fit-content' }}>
              {(() => {
                const allConcepts = curriculum.flatMap(p => p.concepts);
                const current = allConcepts.find(c => c.id === selectedConcept);
                if (!current) return <p>Select a concept on the left to inspect its 9-Problem Mastery Ladder.</p>;

                return (
                  <div>
                    <h3 style={{ fontSize: '1.3rem', fontWeight: '600', marginBottom: '4px' }}>
                      {current.name} Mastery Ladder
                    </h3>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', marginBottom: '20px' }}>
                      {current.component}
                    </p>

                    {current.problems ? (
                      <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
                        {current.problems.map((prob, idx) => (
                          <div 
                            key={idx} 
                            style={{ 
                              display: 'flex', 
                              alignItems: 'flex-start', 
                              gap: '12px', 
                              padding: '12px', 
                              borderRadius: '8px', 
                              background: 'rgba(255,255,255,0.01)', 
                              border: '1px solid var(--border-color)' 
                            }}
                          >
                            <input 
                              type="checkbox" 
                              checked={prob.status === '🟢 Solved'}
                              onChange={() => toggleProblemStatus(current.id, idx)}
                              style={{ marginTop: '3px', cursor: 'pointer' }}
                            />
                            <div style={{ flex: 1 }}>
                              <div style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                                <h4 style={{ fontSize: '0.95rem', fontWeight: '600', textDecoration: prob.status === '🟢 Solved' ? 'line-through' : 'none', color: prob.status === '🟢 Solved' ? 'var(--text-muted)' : 'var(--text-primary)' }}>
                                  {prob.name}
                                </h4>
                                <span style={{ 
                                  fontSize: '0.7rem', 
                                  padding: '2px 5px', 
                                  borderRadius: '3px',
                                  background: prob.difficulty === 'Easy' ? 'rgba(16,185,129,0.1)' : prob.difficulty === 'Medium' ? 'rgba(245,158,11,0.1)' : 'rgba(239,68,68,0.1)',
                                  color: prob.difficulty === 'Easy' ? 'var(--accent-emerald)' : prob.difficulty === 'Medium' ? 'var(--accent-amber)' : 'var(--accent-rose)'
                                }}>
                                  {prob.difficulty}
                                </span>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    ) : (
                      <div style={{ padding: '30px', textAlign: 'center', background: 'rgba(255,255,255,0.01)', borderRadius: '8px', border: '1px dashed var(--border-color)' }}>
                        <BookOpen size={36} color="var(--text-muted)" style={{ margin: '0 auto 10px auto' }} />
                        <h4 style={{ fontSize: '0.95rem', fontWeight: '500', color: 'var(--text-secondary)' }}>Problem Ladder Incoming</h4>
                        <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                          We will populate the 9-problem blueprints for this pattern as you progress through Phase 1.
                        </p>
                      </div>
                    )}
                  </div>
                );
              })()}
            </div>

          </div>
        )}

        {/* 3. CHEATSHEETS TAB */}
        {activeTab === 'cheatsheet' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '30px' }}>
            
            {/* Zero-autoboxing details */}
            <div className="glass-panel" style={{ padding: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                <Cpu size={24} color="var(--accent-indigo)" />
                <h3 style={{ fontSize: '1.2rem', fontWeight: '600' }}>Avoid JVM Autoboxing Overhead</h3>
              </div>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: '1.5', marginBottom: '20px' }}>
                High-throughput system components must avoid standard Java Collection classes (like <code>ArrayList&lt;Integer&gt;</code>) in execution loops. The JVM boxes primitive `int` into heap-allocated `Integer` objects, creating pointer chasing and massive Garbage Collection overhead.
              </p>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                <div style={{ background: 'rgba(239,68,68,0.05)', border: '1px solid rgba(239,68,68,0.2)', padding: '12px', borderRadius: '8px' }}>
                  <h4 style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--accent-rose)', display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <AlertCircle size={14} /> Boxing Trap (Slow Code)
                  </h4>
                  <pre style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: '0.8rem', marginTop: '6px', color: 'var(--text-secondary)' }}>
{`// Heavy object allocations on every insertion
List<Long> timestamps = new ArrayList<>();
timestamps.add(currentTime); // Auto-boxing long -> Long`}
                  </pre>
                </div>

                <div style={{ background: 'rgba(16,185,129,0.05)', border: '1px solid rgba(16,185,129,0.2)', padding: '12px', borderRadius: '8px' }}>
                  <h4 style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--accent-emerald)', display: 'flex', alignItems: 'center', gap: '6px' }}>
                    <CheckCircle2 size={14} /> Zero-Boxing (Fast Code)
                  </h4>
                  <pre style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: '0.8rem', marginTop: '6px', color: 'var(--text-secondary)' }}>
{`// Flat array avoids object allocations
long[] timestamps = new long[LIMIT];
timestamps[tail] = currentTime; // Zero allocations`}
                  </pre>
                </div>
              </div>
            </div>

            {/* Pointer indexing rules */}
            <div className="glass-panel" style={{ padding: '24px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '16px' }}>
                <Code2 size={24} color="var(--accent-indigo)" />
                <h3 style={{ fontSize: '1.2rem', fontWeight: '600' }}>Unsigned Pointer Arithmetic</h3>
              </div>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', lineHeight: '1.5', marginBottom: '20px' }}>
                When binary searching massive arrays (10,000,000+ items), calculating `(low + high) / 2` can exceed `Integer.MAX_VALUE`, wrapping the sum to negative values and throwing index out of bounds exceptions.
              </p>

              <div style={{ background: 'rgba(16,185,129,0.05)', border: '1px solid rgba(16,185,129,0.2)', padding: '12px', borderRadius: '8px', marginBottom: '12px' }}>
                <h4 style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--accent-emerald)', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <CheckCircle2 size={14} /> Overflow-Safe Midpoint Shift
                </h4>
                <pre style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: '0.8rem', marginTop: '6px', color: 'var(--text-secondary)' }}>
{`// Correct: Unsigned right shift operator >>>
int mid = (low + high) >>> 1;`}
                </pre>
              </div>

              <div style={{ background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px', border: '1px solid var(--border-color)' }}>
                <h4 style={{ fontSize: '0.85rem', fontWeight: '600', color: 'var(--text-secondary)' }}>Why it works:</h4>
                <p style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '4px', lineHeight: '1.4' }}>
                  The <code>&gt;&gt;&gt;</code> operator shifts the bit representation to the right, inserting a zero at the most significant bit. Even if <code>low + high</code> overflows and becomes a negative number, the unsigned shift treats it as positive, successfully obtaining the correct midpoint.
                </p>
              </div>
            </div>

          </div>
        )}

      </main>

    </div>
  );
}
