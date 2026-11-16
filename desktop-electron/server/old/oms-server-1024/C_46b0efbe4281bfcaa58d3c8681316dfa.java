import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_46b0efbe4281bfcaa58d3c8681316dfa
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.sysTable_IsSubTable.get(mapRow.get("IS_SUBTABLE"));

    }
}
