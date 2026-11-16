import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_581a46c0d77b3c6dcae72621611f0377
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
         return com.zhuofan.oms.service.FlowServiceExtra.getSendtoDept(mapRow);
    }
}
